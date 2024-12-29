package com.thezayin.background_remover.event

sealed class BackgroundRemoverEvent {
    /**
     * Manually trigger background removal if needed
     * (we’ll do it automatically in init below, so might not be mandatory).
     */
    data object StartRemoval : BackgroundRemoverEvent()

    /**
     * User tapped the Save icon – includes a boolean for PNG or JPG
     */
    data class SaveImage(val asPng: Boolean) : BackgroundRemoverEvent()

    /**
     * Show or handle an error message
     */
    data class ShowError(val message: String) : BackgroundRemoverEvent()

    /**
     * User tapped the back button
     */
    data object OnBackClicked : BackgroundRemoverEvent()

    /**
     * Adjust the smoothing value based on user interaction with the slider
     */
    data class AdjustSmoothing(val value: Int) : BackgroundRemoverEvent()
}
