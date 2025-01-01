package com.thezayin.background.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Merges the background image with the processed image applying transformations.
     *
     * @param background The background Bitmap.
     * @param processed The processed Bitmap to overlay.
     * @param scale Scale factor for the processed image.
     * @param offsetX Horizontal offset for the processed image.
     * @param offsetY Vertical offset for the processed image.
     * @param rotation Rotation angle for the processed image in degrees.
     * @return A new Bitmap with the processed image overlaid on the background.
     */
    fun mergeImages(
        background: Bitmap,
        processed: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        rotation: Float
    ): Bitmap {
        // Create a mutable bitmap with the same dimensions as the background
        val result = background.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        // Prepare transformation matrix
        val matrix = Matrix().apply {
            // Apply scaling
            postScale(scale, scale, 0f, 0f)
            // Apply rotation
            postRotate(rotation, 0f, 0f)
            // Apply translation (position)
            postTranslate(offsetX, offsetY)
        }

        // Draw the processed image onto the background
        canvas.drawBitmap(processed, matrix, null)

        return result
    }

    /**
     * Saves a Bitmap to the internal storage and returns its Uri.
     *
     * @param context The application context.
     * @param bitmap The Bitmap to save.
     * @param filename The name of the file.
     * @return The Uri of the saved image or null if failed.
     */
    fun saveBitmapToInternalStorage(
        context: Context, bitmap: Bitmap, filename: String
    ): Uri? {
        return try {
            val file = File(context.filesDir, "$filename.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}