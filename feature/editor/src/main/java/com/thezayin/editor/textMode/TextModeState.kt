package com.thezayin.editor.textMode

import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.transformableViews.base.TransformableBoxState

data class TextModeState(
    val transformableViewStateList: ArrayList<TransformableBoxState> = arrayListOf(),
    val selectedTool: BottomToolbarItemState = BottomToolbarItemState.NONE,
    val showBottomToolbarExtension: Boolean = false,
    val recompositionTrigger: Long = 0,
    val selectedViewStateUpdateTrigger: Long = 0,   // used separately to trigger recomposition for bottomToolbarExtension
)