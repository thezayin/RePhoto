package com.thezayin.editor.cropMode

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.editor.cropMode.event.CropScreenEvent
import com.thezayin.editor.cropMode.state.CropScreenState
import com.thezayin.editor.editor.domain.repository.ImageRepository
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CropViewModel(
    private val sessionManager: SessionManager,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // Holds the UI state
    private val _state = MutableStateFlow(CropScreenState())
    val state: StateFlow<CropScreenState> = _state.asStateFlow()

    // Channel for one-time UI events (e.g., errors)
    private val _uiEvent = Channel<String>()
    val uiEvent: Flow<String> = _uiEvent.receiveAsFlow()

    // Initialize by fetching the current bitmap from SessionManager
    init {
        viewModelScope.launch {
            val baseImage = sessionManager.getBaseImage()
            val currentBitmap = sessionManager.getCurrentBitmap()
            Log.d("CropViewModel", "Current bitmap: $currentBitmap")

            if (currentBitmap == null) {
                _state.value = CropScreenState(
                    isCropping = false,
                    errorMessage = "No image available for cropping.",
                    currentBitmap = null
                )
                _uiEvent.send("No image available for cropping.")
            } else {
                _state.value = CropScreenState(
                    isCropping = false,
                    errorMessage = null,
                    currentBitmap = currentBitmap
                )
            }
        }
    }

    /**
     * Handles incoming events from the UI.
     */
    fun onEvent(event: CropScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is CropScreenEvent.StartCropping -> {
                    _state.value = _state.value.copy(isCropping = true)
                    // Additional logic if needed
                }

                is CropScreenEvent.CropCompleted -> {
                    handleCropCompleted(event.croppedBitmap)
                }

                is CropScreenEvent.CropCancelled -> {
                    // Handle crop cancellation if necessary
                    _state.value = _state.value.copy(isCropping = false)
                }
            }
        }
    }

    /**
     * Handles the completion of the crop operation.
     */
    private suspend fun handleCropCompleted(croppedBitmap: Bitmap) {
        try {
            // Add the cropped bitmap to the SessionManager's bitmap stack
            sessionManager.addBitmap(croppedBitmap)
            // Update the state with the new bitmap
            _state.value = CropScreenState(
                isCropping = false,
                errorMessage = null,
                currentBitmap = croppedBitmap
            )
            // Optionally, send a success message
            _uiEvent.send("Image cropped successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = CropScreenState(
                isCropping = false,
                errorMessage = "Failed to crop image.",
                currentBitmap = _state.value.currentBitmap
            )
            _uiEvent.send("Failed to crop image.")
        }
    }
}
