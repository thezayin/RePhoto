package com.thezayin.enhance.domain.repository

import android.graphics.Bitmap
import com.thezayin.enhance.domain.model.EnhancementType

interface EnhanceRepository {
    /**
     * Enhances or deblurs the provided bitmap based on the specified [type].
     *
     * @param inputBitmap The original image to be processed.
     * @param type The type of enhancement or deblurring to apply.
     * @return The processed bitmap after enhancement or deblurring.
     * @throws IllegalArgumentException If the [type] is unsupported.
     * @throws Exception For any processing errors.
     */
    suspend fun processImage(inputBitmap: Bitmap, type: EnhancementType): Bitmap
}