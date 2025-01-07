package com.thezayin.enhance.presentation.state

import android.graphics.Bitmap
import android.net.Uri

data class EnhanceState(
    val baseImageUri: Uri? = null,
    val enhancedImage: Bitmap? = null,       // BASIC
    val enhancePlusImage: Bitmap? = null,    // PLUS
    val enhanceProImage: Bitmap? = null,     // PRO
    val deblurImage: Bitmap? = null,         // DEBLUR
    val isLoading: Boolean = false,
    val error: String? = null
)