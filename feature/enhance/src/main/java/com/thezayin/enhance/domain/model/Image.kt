package com.thezayin.enhance.domain.model

import android.graphics.Bitmap

data class Image(
    val id: String,
    val bitmap: Bitmap,
    val clarityLevel: Int // Scale from 1 to 100
)