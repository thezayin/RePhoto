package com.thezayin.background_blur.state

import android.graphics.Bitmap

/**
 * Data class representing the UI state for the Background Blur feature.
 *
 * @param isLoading Indicates if a processing task is ongoing.
 * @param originalBitmap The original image selected by the user.
 * @param blurredImage The image with the background blurred.
 * @param smoothedImage The blurred image with edge smoothing applied.
 * @param displayBitmap The current image to display (blurred or smoothed).
 * @param errorMessage Any error messages to display.
 * @param statusText Status messages during processing.
 * @param currentBlurRadius Current blur intensity value.
 * @param currentSmoothness Current edge smoothness value.
 */
data class BackgroundBlurState(
    val isLoading: Boolean = false,
    val originalBitmap: Bitmap? = null,
    val blurredImage: Bitmap? = null,
    val maskBitmap: Bitmap? = null,        // Added for segmentation mask
    val smoothedImage: Bitmap? = null,
    val displayBitmap: Bitmap? = null,
    val errorMessage: String? = null,
    val statusText: String = "",
    val currentBlurRadius: Int = 0,        // For blur intensity
    val currentSmoothness: Int = 0         // For edge smoothness
)
