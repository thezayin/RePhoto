package com.thezayin.background_remover.state

import android.graphics.Bitmap

data class BackgroundRemoverState(
    val isLoading: Boolean = false,
    val originalBitmap: Bitmap? = null,
    val removedBgBitmap: Bitmap? = null,
    val displayBitmap: Bitmap? = null,
    val errorMessage: String? = null,
    val statusText: String = "",
    val triggerAnimationKey: Int = 0,
    val currentBlurRadius: Int = 0
)