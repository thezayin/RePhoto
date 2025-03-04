package com.thezayin.editor.effectsMode.effectsPreview

import android.graphics.Bitmap
import java.util.UUID

data class EffectItem(
    val id: String = UUID.randomUUID().toString(),
    val ogBitmap: Bitmap,
    val previewBitmap: Bitmap,
    val label: String
)