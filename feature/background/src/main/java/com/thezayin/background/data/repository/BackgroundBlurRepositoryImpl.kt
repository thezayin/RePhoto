package com.thezayin.background.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.thezayin.background.data.blur.BlurUtils
import com.thezayin.background.data.segmentation.SubjectSegmentationHelper
import com.thezayin.background.domain.model.BlurResult
import com.thezayin.background.domain.repository.BackgroundBlurRepository
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of [BackgroundBlurRepository].
 * Manages the base image, applies blur and smoothing effects, and maintains processed images.
 *
 * @param context The Android context required for GPUImage operations.
 */
class BackgroundBlurRepositoryImpl(
    private val context: Context
) : BackgroundBlurRepository {

    companion object {
        private const val TAG = "jejeRepositoryImpl"
    }

    private var baseImage: Bitmap? = null
    private var blurredImage: Bitmap? = null
    private var smoothedImage: Bitmap? = null
    private var currentSmoothnessValue: Int = 0

    // Caches to store previously processed results
    private val blurCache = mutableMapOf<Int, Bitmap>()
    private val smoothnessCache = mutableMapOf<Int, Bitmap>()

    override suspend fun setBaseImage(baseImage: Bitmap) {
        withContext(Dispatchers.Default) {
            this@BackgroundBlurRepositoryImpl.baseImage =
                baseImage.copy(Bitmap.Config.ARGB_8888, true)
            // Reset processed images and caches when a new base image is set
            blurredImage = null
            smoothedImage = null
            currentSmoothnessValue = 0
            blurCache.clear()
            smoothnessCache.clear()
            Log.d(TAG, "setBaseImage: baseImage set successfully and caches cleared.")
        }
    }

    override suspend fun applyBlurToBackground(blurRadius: Int): BlurResult? =
        withContext(Dispatchers.Default) {
            try {
                val originalBitmap = baseImage ?: run {
                    Log.d(TAG, "applyBlurToBackground: baseImage is null.")
                    return@withContext null
                }

                Log.d(TAG, "applyBlurToBackground: Applying blur with radius $blurRadius.")

                // Step 1: Obtain segmentation mask
                val maskBitmap = SubjectSegmentationHelper.getSegmentationMask(originalBitmap)
                    ?: run {
                        Log.d(TAG, "applyBlurToBackground: Failed to obtain segmentation mask.")
                        return@withContext null
                    }

                Log.d(TAG, "applyBlurToBackground: Segmentation mask obtained successfully.")

                // Step 2: Check cache for blurred background
                val blurredBackground = blurCache[blurRadius] ?: run {
                    Log.d(TAG, "applyBlurToBackground: Blurred background not in cache. Applying Gaussian Blur.")

                    val gpuImage = GPUImage(context)
                    gpuImage.setImage(originalBitmap)
                    val blurFilter = GPUImageGaussianBlurFilter(blurRadius.toFloat())
                    gpuImage.setFilter(blurFilter)
                    val blurred = gpuImage.bitmapWithFilterApplied

                    if (blurred == null) {
                        Log.d(TAG, "applyBlurToBackground: Failed to apply Gaussian Blur.")
                        return@withContext null
                    }

                    blurCache[blurRadius] = blurred
                    blurred
                }

                Log.d(TAG, "applyBlurToBackground: Gaussian Blur applied successfully.")

                // Step 3: Merge the original image with the blurred background using the mask
                val mergedBitmap =
                    BlurUtils.mergeWithMask(originalBitmap, blurredBackground, maskBitmap)
                        ?: run {
                            Log.d(TAG, "applyBlurToBackground: Failed to merge images with mask.")
                            return@withContext null
                        }

                Log.d(TAG, "applyBlurToBackground: Images merged with mask successfully.")

                // Update blurredImage and smoothedImage
                blurredImage = blurredBackground

                // Apply current smoothing if any
                if (currentSmoothnessValue > 0) {
                    Log.d(
                        TAG,
                        "applyBlurToBackground: Applying existing edge smoothing with smoothness $currentSmoothnessValue."
                    )
                    val smoothed = applyEdgeSmoothing(currentSmoothnessValue)
                    smoothedImage = smoothed
                    // Return the smoothed image along with other results
                    return@withContext BlurResult(
                        blurredBitmap = blurredBackground,
                        maskBitmap = maskBitmap,
                        mergedBitmap = smoothed
                    )
                } else {
                    // If no smoothing, set smoothedImage as mergedBitmap
                    smoothedImage = mergedBitmap
                    return@withContext BlurResult(
                        blurredBitmap = blurredBackground,
                        maskBitmap = maskBitmap,
                        mergedBitmap = mergedBitmap
                    )
                }
            } catch (e: Exception) {
                Log.d(TAG, "applyBlurToBackground: Exception occurred - ${e.message}")
                null
            }
        }

    override suspend fun applyEdgeSmoothing(smoothness: Int): Bitmap? =
        withContext(Dispatchers.Default) {
            try {
                val originalBitmap = baseImage ?: run {
                    Log.d(TAG, "applyEdgeSmoothing: baseImage is null.")
                    return@withContext null
                }

                Log.d(TAG, "applyEdgeSmoothing: Applying edge smoothing with smoothness: $smoothness.")

                // Re-obtain the segmentation mask to ensure up-to-date smoothing
                val maskBitmap = SubjectSegmentationHelper.getSegmentationMask(originalBitmap)
                    ?: run {
                        Log.d(TAG, "applyEdgeSmoothing: Failed to obtain segmentation mask for smoothing.")
                        return@withContext null
                    }

                Log.d(TAG, "applyEdgeSmoothing: Segmentation mask obtained for smoothing.")

                // Step 1: Check cache for smoothed mask
                val blurredMask = smoothnessCache[smoothness] ?: run {
                    Log.d(TAG, "applyEdgeSmoothing: Smoothed mask not in cache. Applying optimized GPU-based Gaussian blur.")

                    val blurred = BlurUtils.blurMaskWithGPUOptimized(context, maskBitmap, smoothness.toFloat())
                        ?: run {
                            Log.d(TAG, "applyEdgeSmoothing: Failed to apply optimized GPU-based Gaussian blur to mask.")
                            return@withContext null
                        }

                    smoothnessCache[smoothness] = blurred
                    blurred
                }

                Log.d(TAG, "applyEdgeSmoothing: Optimized GPU-based Gaussian blur applied successfully.")

                // Step 2: Merge again with the blurred mask to achieve smooth edges
                val finalBitmap = BlurUtils.mergeWithMask(originalBitmap, blurredImage!!, blurredMask)
                    ?: run {
                        Log.d(TAG, "applyEdgeSmoothing: Failed to merge images with blurred mask.")
                        return@withContext null
                    }

                Log.d(TAG, "applyEdgeSmoothing: Images merged with blurred mask successfully.")

                // Update smoothedImage and save the current smoothing value
                smoothedImage = finalBitmap
                currentSmoothnessValue = smoothness

                finalBitmap
            } catch (e: Exception) {
                Log.d(TAG, "applyEdgeSmoothing: Exception occurred - ${e.message}")
                null
            }
        }

    override suspend fun getProcessedImage(): Bitmap? = withContext(Dispatchers.Default) {
        smoothedImage
    }

    override suspend fun getBlurredImage(): Bitmap? = withContext(Dispatchers.Default) {
        blurredImage
    }
}
