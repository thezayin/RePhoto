package com.thezayin.enhance.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Helper object for Bitmap operations within the Presentation Layer.
 */
object BitmapHelper {
    /**
     * Converts a [Uri] to a [Bitmap].
     *
     * @param context The context for accessing content resolver.
     * @param uri The Uri of the image.
     * @return The decoded Bitmap, or null if decoding fails.
     */
    suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error converting Uri to Bitmap")
                null
            }
        }
    }
}
