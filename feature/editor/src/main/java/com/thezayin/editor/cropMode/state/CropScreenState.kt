package com.thezayin.editor.cropMode.state

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

@Immutable
data class CropScreenState(
    val isCropping: Boolean = false,
    val errorMessage: String? = null,
    val currentBitmap: Bitmap? = null
)