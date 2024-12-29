package com.thezayin.background.data.blur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

object BlurUtils {

    /**
     * Applies Gaussian blur to the alpha channel of the given bitmap.
     *
     * @param bitmap The input bitmap with transparent background.
     * @param radius The radius of the blur. Higher values result in more blur.
     * @return A new bitmap with blurred edges.
     */
    fun blurAlphaChannel(bitmap: Bitmap?, radius: Int): Bitmap {
        if (bitmap == null || radius < 1) return bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Step 1: Extract the alpha channel as a grayscale bitmap
        val alphaBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(alphaBitmap)
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL

        // Draw the alpha channel
        extractAlpha(bitmap, alphaBitmap)

        // Step 2: Apply Gaussian blur to the alpha channel
        val blurredAlpha = gaussianBlur(alphaBitmap, radius)

        // Step 3: Merge the blurred alpha back with the original image
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val outputCanvas = Canvas(outputBitmap)
        val outputPaint = Paint()

        // Draw the original image
        outputCanvas.drawBitmap(bitmap, 0f, 0f, outputPaint)

        // Apply the blurred alpha mask
        val erasePaint = Paint()
        erasePaint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN)
        outputCanvas.drawBitmap(blurredAlpha, 0f, 0f, erasePaint)
        erasePaint.xfermode = null

        return outputBitmap
    }

    /**
     * Extracts the alpha channel from the original bitmap into the provided alphaBitmap.
     *
     * @param original The original bitmap.
     * @param alphaBitmap The bitmap to store the alpha channel.
     */
    private fun extractAlpha(original: Bitmap, alphaBitmap: Bitmap) {
        val width = original.width
        val height = original.height
        val pixels = IntArray(width * height)
        original.getPixels(pixels, 0, width, 0, 0, width, height)

        val alphaPixels = IntArray(width * height)
        for (i in pixels.indices) {
            val alpha = (pixels[i] shr 24) and 0xFF
            // Set grayscale based on alpha
            alphaPixels[i] = Color.argb(alpha, alpha, alpha, alpha)
        }

        alphaBitmap.setPixels(alphaPixels, 0, width, 0, 0, width, height)
    }

    /**
     * Applies a simple Gaussian blur to the given bitmap.
     *
     * @param bitmap The input bitmap to blur.
     * @param radius The radius of the blur. Higher values result in more blur.
     * @return A new blurred bitmap.
     */
    fun gaussianBlur(bitmap: Bitmap, radius: Int): Bitmap {
        if (radius < 1) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Horizontal blur
        val blurredPixels = IntArray(width * height)
        for (y in 0 until height) {
            var a = 0
            var r = 0
            var g = 0
            var b = 0
            var count = 0

            for (x in -radius..radius) {
                val pixel = getPixel(pixels, width, height, x, y)
                a += (pixel shr 24) and 0xFF
                r += (pixel shr 16) and 0xFF
                g += (pixel shr 8) and 0xFF
                b += pixel and 0xFF
                count++
            }

            for (x in 0 until width) {
                blurredPixels[y * width + x] = ((a / count) shl 24) or
                        ((r / count) shl 16) or
                        ((g / count) shl 8) or
                        (b / count)

                // Subtract the pixel leaving the window
                val pixelOut = getPixel(pixels, width, height, x - radius, y)
                a -= (pixelOut shr 24) and 0xFF
                r -= (pixelOut shr 16) and 0xFF
                g -= (pixelOut shr 8) and 0xFF
                b -= pixelOut and 0xFF
                count--

                // Add the pixel entering the window
                val pixelIn = getPixel(pixels, width, height, x + radius + 1, y)
                a += (pixelIn shr 24) and 0xFF
                r += (pixelIn shr 16) and 0xFF
                g += (pixelIn shr 8) and 0xFF
                b += pixelIn and 0xFF
                count++
            }
        }

        // Vertical blur
        val finalPixels = IntArray(width * height)
        for (x in 0 until width) {
            var a = 0
            var r = 0
            var g = 0
            var b = 0
            var count = 0

            for (y in -radius..radius) {
                val pixel = getPixel(blurredPixels, width, height, x, y)
                a += (pixel shr 24) and 0xFF
                r += (pixel shr 16) and 0xFF
                g += (pixel shr 8) and 0xFF
                b += pixel and 0xFF
                count++
            }

            for (y in 0 until height) {
                finalPixels[y * width + x] = ((a / count) shl 24) or
                        ((r / count) shl 16) or
                        ((g / count) shl 8) or
                        (b / count)

                // Subtract the pixel leaving the window
                val pixelOut = getPixel(blurredPixels, width, height, x, y - radius)
                a -= (pixelOut shr 24) and 0xFF
                r -= (pixelOut shr 16) and 0xFF
                g -= (pixelOut shr 8) and 0xFF
                b -= pixelOut and 0xFF
                count--

                // Add the pixel entering the window
                val pixelIn = getPixel(blurredPixels, width, height, x, y + radius + 1)
                a += (pixelIn shr 24) and 0xFF
                r += (pixelIn shr 16) and 0xFF
                g += (pixelIn shr 8) and 0xFF
                b += pixelIn and 0xFF
                count++
            }
        }

        // Create the final blurred bitmap
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        blurredBitmap.setPixels(finalPixels, 0, width, 0, 0, width, height)
        return blurredBitmap
    }

    /**
     * Retrieves the pixel at (x, y). If out of bounds, returns the nearest edge pixel.
     *
     * @param pixels The pixel array.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The pixel color as an integer.
     */
    private fun getPixel(pixels: IntArray, width: Int, height: Int, x: Int, y: Int): Int {
        val clampedX = x.coerceIn(0, width - 1)
        val clampedY = y.coerceIn(0, height - 1)
        return pixels[clampedY * width + clampedX]
    }
}
