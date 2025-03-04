package com.thezayin.editor.textMode.textEditorLayout

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign


sealed class TextEditorEvent {
    data class UpdateTextFieldValue(val textInput: String): TextEditorEvent()
    data class SelectTextColor(val index: Int, val color: Color): TextEditorEvent()
    data class SelectTextAlign(val index: Int, val textAlign: TextAlign): TextEditorEvent()
}