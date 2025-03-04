package com.thezayin.editor.effectsMode

import android.graphics.Bitmap
import com.thezayin.editor.effectsMode.effectsPreview.EffectItem

data class EffectsModeState(
    val filteredBitmap: Bitmap? = null,
    val effectsList: ArrayList<EffectItem> = arrayListOf(),
    val selectedEffectIndex: Int = 0,
//    val recompositionTrigger: Long = 0
)
