package com.thezayin.editor.textMode.bottomToolbarExtension

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.caseOptions.TextCaseType
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.styleOptions.TextStyleAttr

sealed class TextModeToolbarExtensionEvent {

    data class UpdateTextStyleAttr(val textStyleAttr: TextStyleAttr) :
        TextModeToolbarExtensionEvent()

    data class UpdateTextCaseType(val textCaseType: TextCaseType) : TextModeToolbarExtensionEvent()
    data class UpdateTextAlignment(val textAlignment: TextAlign) : TextModeToolbarExtensionEvent()
    data class UpdateTextFontFamily(val fontFamily: FontFamily) : TextModeToolbarExtensionEvent()
}