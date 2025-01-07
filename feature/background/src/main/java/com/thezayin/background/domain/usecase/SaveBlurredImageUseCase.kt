// File: SaveBlurredImageUseCase.kt
package com.thezayin.background.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Use case to save a blurred bitmap and retrieve its Uri.
 *
 * @param context The Android context required for accessing ContentResolver.
 */
class SaveBlurredImageUseCase(
    private val context: Context
) {
    /**
     * Executes the use case to save the blurred image.
     *
     * @param bitmap The bitmap to save.
     * @param filename The desired filename for the saved image.
     * @param asPng Determines the image format (PNG if true, JPEG if false).
     * @return The Uri of the saved image or null if the operation fails.
     */
    suspend operator fun invoke(bitmap: Bitmap, filename: String, asPng: Boolean): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, if (asPng) "image/png" else "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/BlurredImages")
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    contentResolver.openOutputStream(it).use { outputStream ->
                        if (outputStream != null) {
                            bitmap.compress(if (asPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush()
                        } else {
                            Timber.e("Failed to get output stream.")
                            return@withContext null
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        contentResolver.update(it, contentValues, null, null)
                    }

                    return@withContext it
                } ?: run {
                    Timber.e("Failed to create new MediaStore record.")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving bitmap to MediaStore.")
                null
            }
        }
    }
}
