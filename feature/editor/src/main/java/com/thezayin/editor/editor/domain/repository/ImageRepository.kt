package com.thezayin.editor.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface ImageRepository {
    suspend fun getBitmapFromUri(uri: Uri): Bitmap
    suspend fun saveBitmap(bitmap: Bitmap, file: File)
    fun getUriForFile(file: File): Uri?
    fun getCacheDir(): File
}
