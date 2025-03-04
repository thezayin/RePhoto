package com.thezayin.editor.utils.other.bitmap

import android.graphics.Bitmap

sealed class BitmapStatus {
    data object None: BitmapStatus()
    data object Processing: BitmapStatus()
    data class Success(val scaledBitmap: Bitmap): BitmapStatus()
    data class Failed(val exception: Exception? = null, val errorMsg: String? = null): BitmapStatus()
}