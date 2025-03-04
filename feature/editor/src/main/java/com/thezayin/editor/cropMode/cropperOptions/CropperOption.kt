package com.thezayin.editor.cropMode.cropperOptions

import java.util.UUID

data class CropperOption(
    val id: String = UUID.randomUUID().toString(),
    val aspectRatioX: Float,
    val aspectRatioY: Float,
    val label: String
)