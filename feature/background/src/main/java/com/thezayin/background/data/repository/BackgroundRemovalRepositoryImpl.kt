package com.thezayin.background.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.thezayin.background.data.blur.BlurUtils
import com.thezayin.background.data.segmentation.SubjectSegmentationHelper
import com.thezayin.background.domain.repository.BackgroundRemovalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of [BackgroundRemovalRepository].
 * 1. Converts the URI -> Bitmap.
 * 2. Removes background using [SubjectSegmentationHelper].
 */
class BackgroundRemovalRepositoryImpl(
    private val appContext: Context
) : BackgroundRemovalRepository {

    override suspend fun removeBackground(baseImage: Uri?): Bitmap? {
        val baseImageUri: Uri = baseImage ?: return null
        val originalBitmap = uriToBitmap(baseImageUri) ?: run {
            return null
        }

        return SubjectSegmentationHelper.removeBackgroundFromBitmap(originalBitmap)
    }

    /**
     * Safely convert a [Uri] to a [Bitmap].
     */
    private suspend fun uriToBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(appContext.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getOriginalBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(appContext.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Smooths the bitmap edges based on the provided smoothing value.
     *
     * @param bitmap The bitmap to process.
     * @param value The smoothing value (0-100). Higher values result in smoother edges.
     * @return The smoothed bitmap.
     */
    override suspend fun smoothBitmap(bitmap: Bitmap, value: Int): Bitmap =
        withContext(Dispatchers.Default) {
            applySmoothingToBitmap(bitmap, value)
        }

    /**
     * Applies smoothing to the bitmap edges based on the smoothing value.
     *
     * @param bitmap The bitmap to process.
     * @param value The smoothing value (0-100).
     * @return The smoothed bitmap.
     */
    private fun applySmoothingToBitmap(bitmap: Bitmap, value: Int): Bitmap {
        val clampedValue = clamp(value, 0, 100)

        if (clampedValue == 0) {
            return bitmap
        }

        val maxBlurRadius = 20
        val blurRadius = (clampedValue / 100f * maxBlurRadius).toInt()
        val smoothedBitmap = BlurUtils.blurAlphaChannel(bitmap, blurRadius)
        return smoothedBitmap
    }

    /**
     * Clamps the value within the specified range.
     */
    private fun clamp(value: Int, min: Int, max: Int): Int {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }
}