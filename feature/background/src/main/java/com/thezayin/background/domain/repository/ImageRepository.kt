package com.thezayin.background.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {
    /**
     * Merges the background and processed images with specified transformations.
     *
     * @param background The background Bitmap.
     * @param processed The processed Bitmap to overlay.
     * @param scale The scale factor for the processed image.
     * @param offsetX The horizontal offset for the processed image.
     * @param offsetY The vertical offset for the processed image.
     * @param rotation The rotation angle in degrees for the processed image.
     * @return The merged Bitmap, or null if merging fails.
     */
    suspend fun mergeImages(
        background: Bitmap?,
        processed: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        rotation: Float
    ): Bitmap?

    /**
     * Saves the provided Bitmap to internal storage.
     *
     * @param bitmap The Bitmap to save.
     * @param filename The desired filename for the saved image.
     * @return The Uri of the saved image, or null if saving fails.
     */
    suspend fun saveImage(bitmap: Bitmap, filename: String): Uri?
}
