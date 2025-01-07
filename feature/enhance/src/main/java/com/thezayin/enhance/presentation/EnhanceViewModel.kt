package com.thezayin.enhance.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.enhance.domain.model.EnhancementType
import com.thezayin.enhance.domain.usecase.EnhanceImageUseCase
import com.thezayin.enhance.presentation.event.EnhanceEvent
import com.thezayin.enhance.presentation.state.EnhanceState
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class EnhanceViewModel(
    private val application: Application,
    private val enhanceImageUseCase: EnhanceImageUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(EnhanceState())
    val state: StateFlow<EnhanceState> = _state

    init {
        onEvent(EnhanceEvent.LoadBaseImage)
    }

    fun onEvent(event: EnhanceEvent) {
        when (event) {
            is EnhanceEvent.LoadBaseImage -> loadBaseImage()
            is EnhanceEvent.EnhanceNormal -> enhanceImage(EnhancementType.BASIC)
            is EnhanceEvent.EnhancePlus -> enhanceImage(EnhancementType.PLUS)
            is EnhanceEvent.EnhancePro -> enhanceImage(EnhancementType.PRO)
            is EnhanceEvent.Deblur -> enhanceImage(EnhancementType.DEBLUR)
        }
    }

    private fun loadBaseImage() {
        viewModelScope.launch {
            try {
                val uri = sessionManager.getBaseImage().first()
                if (uri == null) {
                    _state.value = _state.value.copy(error = "Base image not found")
                } else {
                    _state.value = _state.value.copy(baseImageUri = uri)
                    // Automatically start BASIC enhancement
                    onEvent(EnhanceEvent.EnhanceNormal)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading base image")
                _state.value = _state.value.copy(error = e.message ?: "Unknown Error")
            }
        }
    }

    private fun enhanceImage(type: EnhancementType) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Retrieve the base image Uri
                val uri = sessionManager.getBaseImage().first()
                if (uri == null) {
                    _state.value =
                        _state.value.copy(error = "Base image not found", isLoading = false)
                    return@launch
                }

                // Convert Uri to Bitmap using BitmapHelper
                val bitmap = BitmapHelper.uriToBitmap(application, uri)
                if (bitmap == null) {
                    _state.value =
                        _state.value.copy(error = "Failed to load image", isLoading = false)
                    return@launch
                }

                // Process the image
                val processedBitmap = enhanceImageUseCase(bitmap, type)

                // Update the state based on the enhancement type
                _state.value = when (type) {
                    EnhancementType.BASIC -> _state.value.copy(
                        enhancedImage = processedBitmap,
                        isLoading = false
                    )

                    EnhancementType.PLUS -> _state.value.copy(
                        enhancePlusImage = processedBitmap,
                        isLoading = false
                    )

                    EnhancementType.PRO -> _state.value.copy(
                        enhanceProImage = processedBitmap,
                        isLoading = false
                    )

                    EnhancementType.DEBLUR -> _state.value.copy(
                        deblurImage = processedBitmap,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error enhancing image with type: $type")
                _state.value = _state.value.copy(
                    error = e.message ?: "Error processing image",
                    isLoading = false
                )
            }
        }
    }
}