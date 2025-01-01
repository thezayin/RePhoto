// File: com/thezayin/background_changer/repository/ImageRepositoryImpl.kt
package com.thezayin.background.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.thezayin.background.data.image.ImageUtils
import com.thezayin.background.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepositoryImpl(private val context: Context) : ImageRepository {
    override suspend fun mergeImages(
        background: Bitmap?,
        processed: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        rotation: Float
    ): Bitmap? {
        return withContext(Dispatchers.Default) {
            try {
                if (background != null) {
                    ImageUtils.mergeImages(
                        background = background,
                        processed = processed,
                        scale = scale,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        rotation = rotation
                    )
                } else {
                    // No background selected, return the processed image as-is
                    processed
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun saveImage(bitmap: Bitmap, filename: String): Uri? {
        return withContext(Dispatchers.IO) {
            ImageUtils.saveBitmapToInternalStorage(
                context = context,
                bitmap = bitmap,
                filename = filename
            )
        }
    }
}
