package com.thezayin.background.domain.model

import android.graphics.Bitmap

/**
 * Represents the result of the blur operation, containing both the blurred bitmap and the segmentation mask.
 */
data class BlurResult(
    val blurredBitmap: Bitmap,
    val maskBitmap: Bitmap,
    val mergedBitmap: Bitmap? // New field
)