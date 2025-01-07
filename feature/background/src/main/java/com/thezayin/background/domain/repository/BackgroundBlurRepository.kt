package com.thezayin.background.domain.repository

import android.graphics.Bitmap
import com.thezayin.background.domain.model.BlurResult

/**
 * Repository interface for handling background blurring operations.
 */
interface BackgroundBlurRepository {

    /**
     * Sets the base image to be processed.
     *
     * @param baseImage The original image as a Bitmap.
     */
    suspend fun setBaseImage(baseImage: Bitmap)

    /**
     * Applies blur to the background of the base image.
     *
     * @param blurRadius The radius of the blur effect.
     * @return The [BlurResult] containing the blurred Bitmap and segmentation mask, or null if the operation fails.
     */
    suspend fun applyBlurToBackground(blurRadius: Int): BlurResult?

    /**
     * Applies edge smoothing to the blurred image.
     *
     * @param smoothness The smoothness level for edge smoothing.
     * @return The smoothed Bitmap or null if the operation fails.
     */
    suspend fun applyEdgeSmoothing(smoothness: Int): Bitmap?

    /**
     * Retrieves the processed (smoothed) image.
     *
     * @return The processed Bitmap or null if not available.
     */
    suspend fun getProcessedImage(): Bitmap?

    /**
     * Retrieves the blurred image without edge smoothing.
     *
     * @return The blurred Bitmap or null if not available.
     */
    suspend fun getBlurredImage(): Bitmap?
}
