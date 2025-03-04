package com.thezayin.editor.textMode

import com.thezayin.editor.textMode.textEditorLayout.TextEditorState
import com.thezayin.editor.transformableViews.base.TransformableTextBoxState
import com.thezayin.editor.transformableViews.base.TransformableBoxState


sealed class TextModeEvent {
    data class UpdateTransformableViewsList(val list: ArrayList<TransformableBoxState>): TextModeEvent()
    data class AddTransformableTextBox(val textBoxState: TransformableTextBoxState): TextModeEvent()
    data class ShowTextEditor(val textEditorState: TextEditorState? = null): TextModeEvent()
    data class UpdateToolbarExtensionVisibility(val isVisible: Boolean): TextModeEvent()
    data object HideTextEditor: TextModeEvent()
}