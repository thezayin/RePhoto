package com.thezayin.background.domain.repository

import android.graphics.Bitmap
import android.net.Uri

/**
 * Repository interface for background removal related functionality.
 */
interface BackgroundRemovalRepository {

    /**
     * Fetch the base image (from SessionManager or otherwise),
     * remove its background, and return the resulting Bitmap.
     */
    suspend fun removeBackground(baseImage: Uri?): Bitmap?

    /**
     * Retrieves the original bitmap from the given URI.
     * @param uri The URI of the image.
     * @return The original Bitmap, or null if retrieval fails.
     */
    suspend fun getOriginalBitmap(uri: Uri): Bitmap?

    /**
     * Smooths the bitmap edges based on the smoothing value.
     * @param bitmap The bitmap to smooth.
     * @param value Smoothing value from 0 to 100.
     * @return The smoothed bitmap.
     */
    suspend fun smoothBitmap(bitmap: Bitmap, value: Int): Bitmap
}
