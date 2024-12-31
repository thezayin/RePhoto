package com.thezayin.background.data.blur

import android.graphics.Bitmap

object BlurUtils {

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
