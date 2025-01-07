package com.thezayin.background_blur.event

import android.net.Uri

/**
 * Sealed class representing all possible events in the Background Blur feature.
 */
sealed class BackgroundBlurEvent {
    /**
     * Event triggered when the user selects an image.
     *
     * @param uri The URI of the selected image.
     */
    data class LoadImage(val uri: Uri) : BackgroundBlurEvent()

    /**
     * Event triggered when the user adjusts the blur intensity slider.
     *
     * @param blurRadius The new blur radius value.
     */
    data class UpdateBlurIntensity(val blurRadius: Int) : BackgroundBlurEvent()

    /**
     * Event triggered when the user adjusts the edge smoothness slider.
     *
     * @param smoothness The new smoothness value.
     */
    data class UpdateEdgeSmoothness(val smoothness: Int) : BackgroundBlurEvent()

    /**
     * Event triggered when the user opts to save the processed image.
     *
     * @param filename The desired filename for the saved image.
     * @param asPng Determines the image format (PNG if true, JPEG if false).
     */
    data class SaveImage(val filename: String, val asPng: Boolean) : BackgroundBlurEvent()

    /**
     * Event to display an error message.
     *
     * @param message The error message to display.
     */
    data class ShowError(val message: String) : BackgroundBlurEvent()

    /**
     * Event triggered when the back button is clicked.
     */
    data object OnBackClicked : BackgroundBlurEvent()
}