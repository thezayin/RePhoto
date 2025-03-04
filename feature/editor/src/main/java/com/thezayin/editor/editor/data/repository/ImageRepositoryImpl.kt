package com.thezayin.editor.editor.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.thezayin.editor.editor.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class ImageRepositoryImpl(private val context: Context) : ImageRepository {

    override suspend fun getBitmapFromUri(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        requireNotNull(inputStream) { "Unable to open InputStream from Uri" }
        BitmapFactory.decodeStream(inputStream)
            ?: throw IllegalArgumentException("Unable to decode Bitmap from Uri")
    }

    override suspend fun saveBitmap(bitmap: Bitmap, file: File) = withContext(Dispatchers.IO) {
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
    }

    override fun getUriForFile(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun getCacheDir(): File = context.cacheDir
}
