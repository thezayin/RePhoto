package com.thezayin.background_blur

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.background.domain.usecase.AdjustBlurIntensityUseCase
import com.thezayin.background.domain.usecase.ApplyBlurToBackgroundUseCase
import com.thezayin.background.domain.usecase.ApplySmoothingUseCase
import com.thezayin.background.domain.usecase.SaveBlurredImageUseCase
import com.thezayin.background.domain.usecase.SetBaseImageUseCase
import com.thezayin.background_blur.event.BackgroundBlurEvent
import com.thezayin.background_blur.state.BackgroundBlurState
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.thezayin.values.R

/**
 * ViewModel for managing the Background Blur feature.
 *
 * @param application The Android application context.
 * @param sessionManager Manages session-related data like the selected image URI.
 * @param applyBlurToBackgroundUseCase Use case for applying blur to the background.
 * @param applySmoothingUseCase Use case for applying edge smoothing to the blurred image.
 * @param adjustBlurIntensityUseCase Use case for adjusting the blur intensity.
 * @param saveBlurredImageUseCase Use case for saving the processed image.
 * @param setBaseImageUseCase Use case for setting the base image.
 */
class BackgroundBlurViewModel(
    application: Application,
    private val sessionManager: SessionManager,
    private val setBaseImageUseCase: SetBaseImageUseCase,
    private val applyBlurToBackgroundUseCase: ApplyBlurToBackgroundUseCase,
    private val applySmoothingUseCase: ApplySmoothingUseCase,
    private val adjustBlurIntensityUseCase: AdjustBlurIntensityUseCase,
    private val saveBlurredImageUseCase: SaveBlurredImageUseCase
) : AndroidViewModel(application) {

    companion object {
        private const val DEFAULT_BLUR_RADIUS = 8
        private const val DEFAULT_SMOOTHNESS = 4
    }

    // Internal mutable state
    private val _state = MutableStateFlow(BackgroundBlurState())

    // Public immutable state
    val state: StateFlow<BackgroundBlurState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe the base image URI from SessionManager
            sessionManager.getBaseImage().collectLatest { uri ->
                uri?.let {
                    handleEvent(BackgroundBlurEvent.LoadImage(it))
                }
            }
        }
    }

    /**
     * Handles incoming events and updates the state accordingly.
     *
     * @param event The event to handle.
     */
    fun handleEvent(event: BackgroundBlurEvent) {
        when (event) {
            is BackgroundBlurEvent.LoadImage -> {
                loadImage(event.uri)
            }

            is BackgroundBlurEvent.UpdateBlurIntensity -> {
                updateBlurIntensity(event.blurRadius)
            }

            is BackgroundBlurEvent.UpdateEdgeSmoothness -> {
                updateEdgeSmoothness(event.smoothness)
            }

            is BackgroundBlurEvent.SaveImage -> {
                saveImage(event.filename, event.asPng)
            }

            is BackgroundBlurEvent.ShowError -> {
                showError(event.message)
            }

            BackgroundBlurEvent.OnBackClicked -> {
                // Handle back navigation if necessary
                // This can be managed by the UI layer
            }
        }
    }

    /**
     * Loads the image from the provided URI and initiates blurring.
     *
     * @param uri The URI of the image to load.
     */
    private fun loadImage(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                statusText = getApplication<Application>().getString(R.string.status_loading_image)
            )

            val originalBitmap = getBitmapFromUri(uri)
            if (originalBitmap != null) {
                setBaseImageUseCase.invoke(originalBitmap)

                _state.value = _state.value.copy(
                    originalBitmap = originalBitmap,
                    isLoading = false,
                    statusText = getApplication<Application>().getString(R.string.status_image_loaded)
                )
                // Initiate blurring with a default radius
                updateBlurIntensity(DEFAULT_BLUR_RADIUS)
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_load_image)
                )
            }
        }
    }

    /**
     * Applies blur intensity to the background of the loaded image.
     *
     * @param blurRadius The radius of the blur effect.
     */
    private fun updateBlurIntensity(blurRadius: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                statusText = getApplication<Application>().getString(R.string.status_applying_blur)
            )

            val blurResult = applyBlurToBackgroundUseCase.invoke(blurRadius)
            if (blurResult != null) {
                // Update the state with the merged image
                _state.value = _state.value.copy(
                    blurredImage = blurResult.blurredBitmap,
                    maskBitmap = blurResult.maskBitmap,
                    displayBitmap = blurResult.mergedBitmap,
                    isLoading = false,
                    statusText = getApplication<Application>().getString(R.string.status_blur_applied),
                    currentBlurRadius = blurRadius
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_apply_blur)
                )
            }
        }
    }

    /**
     * Applies edge smoothing to the blurred background image.
     *
     * @param smoothness The smoothness level (e.g., blur radius for smoothing).
     */
    private fun updateEdgeSmoothness(smoothness: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                statusText = getApplication<Application>().getString(R.string.status_smoothing_edges)
            )

            val smoothedBitmap = applySmoothingUseCase.invoke(smoothness)
            if (smoothedBitmap != null) {
                _state.value = _state.value.copy(
                    smoothedImage = smoothedBitmap,
                    displayBitmap = smoothedBitmap,
                    isLoading = false,
                    statusText = getApplication<Application>().getString(R.string.status_edges_smoothed),
                    currentSmoothness = smoothness
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_smooth_edges)
                )
            }
        }
    }

    /**
     * Saves the processed image in the specified format.
     *
     * @param filename The desired filename for the saved image.
     * @param asPng Determines the image format (PNG if true, JPEG if false).
     */
    private fun saveImage(filename: String, asPng: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                statusText = getApplication<Application>().getString(R.string.status_saving_image)
            )

            val bitmapToSave = _state.value.displayBitmap ?: run {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_no_image_to_save)
                )
                return@launch
            }

            val savedUri = saveBlurredImageUseCase.invoke(bitmapToSave, filename, asPng)
            if (savedUri != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    statusText = if (asPng)
                        getApplication<Application>().getString(R.string.status_image_saved_png)
                    else
                        getApplication<Application>().getString(R.string.status_image_saved_jpeg)
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_save_image)
                )
            }
        }
    }

    /**
     * Converts a Uri to a Bitmap.
     *
     * @param uri The URI of the image to convert.
     * @return The converted Bitmap or null if an error occurs.
     */
    private suspend fun getBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(
                    getApplication<Application>().contentResolver, uri
                )
                android.graphics.ImageDecoder.decodeBitmap(source)
            } else {
                android.provider.MediaStore.Images.Media.getBitmap(
                    getApplication<Application>().contentResolver, uri
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Displays an error message on the state.
     *
     * @param msg The error message to display.
     */
    private fun showError(msg: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            errorMessage = msg,
            statusText = ""
        )
    }
}
