package com.thezayin.background.data.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import timber.log.Timber
import kotlin.system.measureTimeMillis

object BlurUtils {
    private const val TAG = "BlurUtils"

    /**
     * Applies Gaussian blur to the segmentation mask using GPUImage.
     *
     * @param context The Android context.
     * @param maskBitmap The original segmentation mask Bitmap.
     * @param blurRadius The radius for the Gaussian blur.
     * @return The blurred mask Bitmap or null if operation fails.
     */
    fun blurMaskWithGPU(context: Context, maskBitmap: Bitmap, blurRadius: Float): Bitmap? {
        var blurredMask: Bitmap? = null
        val timeTaken = measureTimeMillis {
            try {
                Timber.tag(TAG)
                    .d("Applying GPU-based Gaussian blur to mask with radius: $blurRadius")

                val gpuImage = GPUImage(context)
                gpuImage.setImage(maskBitmap)

                val blurFilter = GPUImageGaussianBlurFilter(blurRadius)
                gpuImage.setFilter(blurFilter)

                blurredMask = gpuImage.bitmapWithFilterApplied

                Timber.tag(TAG).d("GPU-based Gaussian blur applied successfully.")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error applying GPU-based Gaussian blur to mask.")
            }
        }
        Timber.tag(TAG).d("blurMaskWithGPU took $timeTaken ms")
        return blurredMask
    }

    /**
     * Applies optimized Gaussian blur to the mask using GPUImage with scaling.
     *
     * @param context The Android context.
     * @param maskBitmap The original segmentation mask Bitmap.
     * @param blurRadius The radius for the Gaussian blur.
     * @param scaleFactor The factor by which to scale down the mask before blurring.
     * @return The blurred mask Bitmap or null if operation fails.
     */
    fun blurMaskWithGPUOptimized(
        context: Context,
        maskBitmap: Bitmap,
        blurRadius: Float,
        scaleFactor: Float = 0.5f
    ): Bitmap? {
        var blurredMask: Bitmap? = null
        val timeTaken = measureTimeMillis {
            try {
                Timber.tag(TAG)
                    .d("Starting optimized GPU-based Gaussian blur with scale factor: $scaleFactor")

                // Step 1: Scale down the mask
                val scaledMask = scaleBitmap(maskBitmap, scaleFactor)

                // Step 2: Apply Gaussian blur using GPUImage
                val gpuImage = GPUImage(context)
                gpuImage.setImage(scaledMask)
                val blurFilter = GPUImageGaussianBlurFilter(blurRadius)
                gpuImage.setFilter(blurFilter)
                val blurredScaledMask = gpuImage.bitmapWithFilterApplied
                    ?: run {
                        Timber.tag(TAG).d("Failed to apply Gaussian Blur to scaled mask.")
                        return null
                    }

                Timber.tag(TAG).d("Gaussian Blur applied to scaled mask successfully.")

                // Step 3: Scale back up to original size
                blurredMask =
                    scaleBitmapBack(blurredScaledMask, maskBitmap.width, maskBitmap.height)

                Timber.tag(TAG).d("Blurred mask scaled back to original size successfully.")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error applying optimized GPU-based Gaussian blur to mask.")
            }
        }
        Timber.tag(TAG).d("blurMaskWithGPUOptimized took $timeTaken ms")
        return blurredMask
    }

    /**
     * Scales a bitmap by the given scale factor.
     *
     * @param bitmap The bitmap to scale.
     * @param scaleFactor The scale factor (e.g., 0.5f to reduce size by half).
     * @return The scaled bitmap.
     */
    fun scaleBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scaleFactor, scaleFactor)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }

    /**
     * Scales a bitmap back to its original size after processing.
     *
     * @param bitmap The bitmap to scale.
     * @param originalWidth The original width.
     * @param originalHeight The original height.
     * @return The scaled bitmap.
     */
    fun scaleBitmapBack(bitmap: Bitmap, originalWidth: Int, originalHeight: Int): Bitmap {
        val matrix = Matrix()
        val scaleX = originalWidth.toFloat() / bitmap.width
        val scaleY = originalHeight.toFloat() / bitmap.height
        matrix.postScale(scaleX, scaleY)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }

    /**
     * Merges the original image with the blurred background using the mask.
     *
     * @param originalBitmap The original bitmap.
     * @param blurredBackground The blurred background bitmap.
     * @param maskBitmap The segmentation mask bitmap where white represents the subject.
     * @return A new merged bitmap or null if failed.
     */
    fun mergeWithMask(
        originalBitmap: Bitmap,
        blurredBackground: Bitmap,
        maskBitmap: Bitmap
    ): Bitmap? {
        return try {
            Timber.tag(TAG)
                .d("Merging images with mask. Dimensions: ${originalBitmap.width}x${originalBitmap.height}")

            // Ensure all bitmaps are the same size
            if (originalBitmap.width != blurredBackground.width || originalBitmap.height != blurredBackground.height ||
                originalBitmap.width != maskBitmap.width || originalBitmap.height != maskBitmap.height
            ) {
                Timber.tag(TAG).e("All bitmaps must have the same dimensions.")
                return null
            }

            val width = originalBitmap.width
            val height = originalBitmap.height
            val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val originalPixels = IntArray(width * height)
            val blurredPixels = IntArray(width * height)
            val maskPixels = IntArray(width * height)

            originalBitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)
            blurredBackground.getPixels(blurredPixels, 0, width, 0, 0, width, height)
            maskBitmap.getPixels(maskPixels, 0, width, 0, 0, width, height)

            val resultPixels = IntArray(width * height)

            for (i in 0 until width * height) {
                val maskAlpha = (maskPixels[i] shr 24) and 0xFF
                val alphaRatio = maskAlpha / 255f

                val originalPixel = originalPixels[i]
                val blurredPixel = blurredPixels[i]

                // Blend based on mask alpha
                val blendedPixel = blendPixels(originalPixel, blurredPixel, alphaRatio)

                resultPixels[i] = blendedPixel
            }

            resultBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)
            resultBitmap
        } catch (e: Exception) {
            Timber.tag(TAG).e("Error merging images with mask: ${e.message}")
            null
        }
    }

    /**
     * Blends two pixels based on the given ratio.
     *
     * @param original The original pixel color.
     * @param blurred The blurred pixel color.
     * @param ratio The blending ratio (0.0f to 1.0f).
     * @return The blended pixel color.
     */
    private fun blendPixels(original: Int, blurred: Int, ratio: Float): Int {
        val rOriginal = (original shr 16) and 0xFF
        val gOriginal = (original shr 8) and 0xFF
        val bOriginal = original and 0xFF

        val rBlurred = (blurred shr 16) and 0xFF
        val gBlurred = (blurred shr 8) and 0xFF
        val bBlurred = blurred and 0xFF

        val r = (rOriginal * ratio + rBlurred * (1 - ratio)).toInt().coerceIn(0, 255)
        val g = (gOriginal * ratio + gBlurred * (1 - ratio)).toInt().coerceIn(0, 255)
        val b = (bOriginal * ratio + bBlurred * (1 - ratio)).toInt().coerceIn(0, 255)

        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }

    /**
     * Applies Gaussian blur to the alpha channel of the given bitmap without using Paint or Canvas.
     *
     * @param bitmap The input bitmap with transparent background.
     * @param radius The radius of the blur. Higher values result in more blur.
     * @return A new bitmap with blurred edges.
     */
    fun blurAlphaChannel(bitmap: Bitmap?, radius: Int): Bitmap {
        if (bitmap == null || radius < 1) {
            return bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        // Step 1: Extract the alpha channel as a grayscale array
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Extract alpha channel into a separate array
        val alphaPixels = IntArray(width * height)
        for (i in pixels.indices) {
            val alpha = (pixels[i] shr 24) and 0xFF
            // Set grayscale based on alpha
            alphaPixels[i] = alpha
        }

        // Step 2: Apply Gaussian blur to the alpha channel
        val blurredAlpha = gaussianBlur(alphaPixels, width, height, radius)

        // Step 3: Merge the blurred alpha back with the original image
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val outputPixels = IntArray(width * height)
        bitmap.getPixels(outputPixels, 0, width, 0, 0, width, height)

        for (i in outputPixels.indices) {
            val originalPixel = outputPixels[i]
            val blurredAlphaValue = blurredAlpha[i]
            // Replace the alpha channel with the blurred alpha
            val newPixel = (blurredAlphaValue shl 24) or (originalPixel and 0x00FFFFFF)
            outputPixels[i] = newPixel
        }

        outputBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)

        // Recycle intermediate arrays
        // Note: No Bitmap objects to recycle since we're only using arrays

        return outputBitmap
    }

    /**
     * Applies a simple Gaussian blur to the given alpha pixel array.
     *
     * @param alphaPixels The alpha pixel array.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param radius The radius of the blur.
     * @return A new array with blurred alpha values.
     */
    private fun gaussianBlur(
        alphaPixels: IntArray,
        width: Int,
        height: Int,
        radius: Int
    ): IntArray {
        if (radius < 1) return alphaPixels.clone()

        val blurred = IntArray(width * height)

        // Horizontal blur
        val horizontalBlur = IntArray(width * height)
        for (y in 0 until height) {
            var sum = 0
            var count = 0
            for (x in -radius..radius) {
                val clampedX = x.coerceIn(0, width - 1)
                val pixel = alphaPixels[y * width + clampedX]
                sum += pixel
                count++
            }

            for (x in 0 until width) {
                horizontalBlur[y * width + x] = sum / count

                // Subtract the pixel leaving the window
                val pixelOutX = x - radius
                val clampedOutX = pixelOutX.coerceIn(0, width - 1)
                sum -= alphaPixels[y * width + clampedOutX]
                count--

                // Add the pixel entering the window
                val pixelInX = x + radius + 1
                val clampedInX = pixelInX.coerceIn(0, width - 1)
                sum += alphaPixels[y * width + clampedInX]
                count++
            }
        }

        // Vertical blur
        for (x in 0 until width) {
            var sum = 0
            var count = 0
            for (y in -radius..radius) {
                val clampedY = y.coerceIn(0, height - 1)
                val pixel = horizontalBlur[clampedY * width + x]
                sum += pixel
                count++
            }

            for (y in 0 until height) {
                blurred[y * width + x] = sum / count

                // Subtract the pixel leaving the window
                val pixelOutY = y - radius
                val clampedOutY = pixelOutY.coerceIn(0, height - 1)
                sum -= horizontalBlur[clampedOutY * width + x]
                count--

                // Add the pixel entering the window
                val pixelInY = y + radius + 1
                val clampedInY = pixelInY.coerceIn(0, height - 1)
                sum += horizontalBlur[clampedInY * width + x]
                count++
            }
        }

        return blurred
    }
}
