package com.thezayin.background_remover

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.background.domain.usecase.RemoveBackgroundUseCase
import com.thezayin.background.domain.usecase.SmoothImageUseCase
import com.thezayin.background_remover.event.BackgroundRemoverEvent
import com.thezayin.background_remover.state.BackgroundRemoverState
import com.thezayin.framework.extension.saveBitmapToGallery
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for handling background removal and image smoothing.
 */
class BackgroundRemoverViewModel(
    private val removeBackgroundUseCase: RemoveBackgroundUseCase,
    private val smoothImageUseCase: SmoothImageUseCase,
    private val sessionManager: SessionManager,
    application: Application
) : AndroidViewModel(application) {

    private val TAG = "BackgroundRemoverViewModel"

    private val _state = MutableStateFlow(BackgroundRemoverState())
    val state: StateFlow<BackgroundRemoverState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            val selectedUri = sessionManager.getBaseImage().firstOrNull()
            if (selectedUri == null) {
                showError("No image found in session. Please pick one again.")
            } else {
                val originalBitmap = removeBackgroundUseCase.getOriginalBitmap(selectedUri)
                if (originalBitmap == null) {
                    showError("Failed to load the original image.")
                } else {
                    _state.update { it.copy(originalBitmap = originalBitmap) }
                    simulateLoadingSteps()
                    removeBackground(selectedUri)
                }
            }
        }
    }

    /**
     * Handle UI events like saving the image or going back.
     */
    fun handleEvent(event: BackgroundRemoverEvent) {
        when (event) {
            is BackgroundRemoverEvent.StartRemoval -> {
                viewModelScope.launch {
                    val uri = sessionManager.getBaseImage().firstOrNull()
                    if (uri == null) showError("No image in session.")
                    else removeBackground(uri)
                }
            }

            is BackgroundRemoverEvent.SaveImage -> {
                saveImage(event.asPng)
            }

            is BackgroundRemoverEvent.ShowError -> {
                showError(event.message)
            }

            BackgroundRemoverEvent.OnBackClicked -> Unit
            is BackgroundRemoverEvent.AdjustSmoothing -> {
                adjustSmoothing(event.value)
            }
        }
    }

    /**
     * Simulate loading steps with different status messages.
     */
    private suspend fun simulateLoadingSteps() {
        val steps = listOf(
            "AI is analyzing the image...", "Removing background...", "Finalizing..."
        )
        _state.update { it.copy(isLoading = true) }
        for (msg in steps) {
            _state.update { it.copy(statusText = msg) }
            delay(1000)
        }
    }

    /**
     * Actually remove the background using ML Kit via [RemoveBackgroundUseCase].
     */
    private fun removeBackground(uri: Uri) {
        _state.update { it.copy(isLoading = true, statusText = "Removing background...") }
        viewModelScope.launch {
            try {
                val resultBitmap: Bitmap? = removeBackgroundUseCase(uri)
                if (resultBitmap != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            removedBgBitmap = resultBitmap,
                            statusText = "Background removed successfully!",
                            triggerAnimationKey = it.triggerAnimationKey + 1,
                            displayBitmap = resultBitmap,
                            currentBlurRadius = 0
                        )
                    }
                } else {
                    showError("Could not remove background.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Error removing background: ${e.message}")
            }
        }
    }

    /**
     * Save the final image as PNG (transparent) or JPG (no transparency).
     */
    private fun saveImage(asPng: Boolean) {
        val bitmap = _state.value.displayBitmap
        if (bitmap == null) {
            showError("No image to save.")
            return
        }
        _state.update { it.copy(isLoading = true, statusText = "Saving...") }

        viewModelScope.launch {
            try {
                saveBitmapToGallery(
                    context = getApplication<Application>().applicationContext,
                    bitmap = bitmap,
                    asPng = asPng
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        statusText = if (asPng) "Image saved as PNG!" else "Image saved as JPG!"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Failed to save image: ${e.message}")
            }
        }
    }

    /**
     * Show an error message on the state.
     */
    private fun showError(msg: String) {
        _state.update {
            it.copy(
                isLoading = false, errorMessage = msg, statusText = ""
            )
        }
    }

    /**
     * Adjust smoothing based on the slider value.
     *
     * @param value The smoothing value (0-100).
     */
    private fun adjustSmoothing(value: Int) {
        viewModelScope.launch {
            val clampedValue = clamp(value, 0, 100)
            _state.update { it.copy(currentBlurRadius = clampedValue) }
            val smoothedBitmap = smoothImageUseCase.invoke(
                _state.value.removedBgBitmap ?: return@launch, clampedValue
            )
            _state.update { it.copy(displayBitmap = smoothedBitmap) }
        }
    }
}