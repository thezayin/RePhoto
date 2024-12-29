package com.thezayin.background.data.segmentation

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.Subject
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Helper object that uses ML Kit's Subject Segmentation API to remove backgrounds.
 */
object SubjectSegmentationHelper {

    private const val TAG = "SubjectSegmentationHelper"

    // ML Kit SubjectSegmenter client
    private val client = SubjectSegmentation.getClient(
        SubjectSegmenterOptions.Builder()
            .enableMultipleSubjects(
                SubjectSegmenterOptions.SubjectResultOptions.Builder()
                    .enableConfidenceMask()
                    .build()
            )
            .build()
    )

    /**
     * Removes the background from the provided bitmap using ML Kit's Subject Segmentation.
     * @param originalBitmap The original bitmap from which to remove the background.
     * @return A new bitmap with the background removed, or null if processing fails.
     */
    suspend fun removeBackgroundFromBitmap(originalBitmap: Bitmap): Bitmap? =
        withContext(Dispatchers.Default) { // Use Default dispatcher for CPU-intensive tasks
            // Unconditionally create a software-backed, mutable copy
            val safeOriginal = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Prepare InputImage
            val inputImage = runCatching {
                InputImage.fromBitmap(safeOriginal, 0)
            }.onFailure { e ->
                Timber.tag(TAG).e("Failed to create InputImage from bitmap: ${e.message}")
            }.getOrNull() ?: return@withContext null

            // Perform subject segmentation
            val segmentationResult: SubjectSegmentationResult = runCatching {
                client.process(inputImage).await()
            }.onFailure { e ->
                Timber.tag(TAG).e("Subject segmentation failed: ${e.message}")
            }.getOrNull() ?: return@withContext null

            // Build a mask Bitmap from the subject confidence masks
            val maskBitmap = Bitmap.createBitmap(
                maskColorsFromSubjects(
                    segmentationResult.subjects,
                    inputImage.height,
                    inputImage.width
                ),
                inputImage.width,
                inputImage.height,
                Bitmap.Config.ARGB_8888
            )

            // Merge the original bitmap & mask into a new Bitmap
            val resultBitmap = applyMask(safeOriginal, maskBitmap)
            return@withContext resultBitmap
        }

    /**
     * Merges the original bitmap with the mask to produce a bitmap with the background removed.
     * Utilizes bulk pixel operations for efficiency.
     * @param original The original software-backed bitmap.
     * @param mask The mask bitmap indicating foreground regions.
     * @return A new bitmap with the background removed.
     */
    private fun applyMask(original: Bitmap, mask: Bitmap): Bitmap {
        val width = original.width
        val height = original.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Initialize pixel arrays
        val originalPixels = IntArray(width * height)
        val maskPixels = IntArray(width * height)
        val resultPixels = IntArray(width * height)

        // Retrieve pixel data
        original.getPixels(originalPixels, 0, width, 0, 0, width, height)
        mask.getPixels(maskPixels, 0, width, 0, 0, width, height)

        // Apply mask: retain original pixel if mask pixel is non-zero; else make transparent
        for (i in originalPixels.indices) {
            resultPixels[i] = if (maskPixels[i] != 0) {
                originalPixels[i]
            } else {
                Color.TRANSPARENT
            }
        }

        // Set the processed pixels to the result bitmap
        result.setPixels(resultPixels, 0, width, 0, 0, width, height)
        return result
    }

    // Example color array for differentiating multiple subjects (if needed)
    private val COLORS =
        arrayOf(
            intArrayOf(255, 0, 255),
            intArrayOf(0, 255, 255),
            intArrayOf(255, 255, 0),
            intArrayOf(255, 0, 0),
            intArrayOf(0, 255, 0),
            intArrayOf(0, 0, 255),
            intArrayOf(128, 0, 128),
            intArrayOf(0, 128, 128),
            intArrayOf(128, 128, 0),
            intArrayOf(128, 0, 0),
            intArrayOf(0, 128, 0),
            intArrayOf(0, 0, 128)
        )

    /**
     * Converts the subject confidence masks into an [IntArray] that represents
     * a colored mask (ARGB_8888).
     * @param subjects List of detected subjects.
     * @param imageHeight Height of the original image.
     * @param imageWidth Width of the original image.
     * @return An IntArray representing the colored mask.
     */
    @ColorInt
    private fun maskColorsFromSubjects(
        subjects: List<Subject>,
        imageHeight: Int,
        imageWidth: Int
    ): IntArray {
        @ColorInt val colors = IntArray(imageWidth * imageHeight) { Color.TRANSPARENT }

        for ((index, subject) in subjects.withIndex()) {
            val rgb = COLORS[index % COLORS.size]
            val subjectColor = Color.argb(128, rgb[0], rgb[1], rgb[2])
            val mask = subject.confidenceMask ?: continue

            for (j in 0 until subject.height) {
                for (i in 0 until subject.width) {
                    if (mask.get() > 0.5) {
                        val pixelX = subject.startX + i
                        val pixelY = subject.startY + j

                        // Ensure we don't go out of bounds
                        if (pixelX in 0 until imageWidth && pixelY in 0 until imageHeight) {
                            val offset = pixelY * imageWidth + pixelX
                            colors[offset] = subjectColor
                        }
                    }
                }
            }

            mask.rewind()
        }
        return colors
    }
}