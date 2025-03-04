package com.thezayin.editor.textMode.textEditorLayout

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.thezayin.editor.utils.ColorUtils

data class TextEditorState(
    val textStateId: String,
    val textFont: TextUnit, /* This has to be updated when initiating TextModeScreen */
    val shouldRequestFocus: Boolean = true, /* initial value is true as the textField is visible initially */
    val text: String = "",
    val textAlign: TextAlign = TextAlign.Center,
    val textColorList: ArrayList<Color> = ColorUtils.defaultColorList,
    val selectedColor: Color = Color.White,
)