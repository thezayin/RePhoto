package com.thezayin.editor.editor.presentation.utils

import com.thezayin.editor.editor.presentation.state.EditorToolbarState

object EditorUtils {

    fun getDefaultBottomToolbarItemsList(): ArrayList<EditorToolbarState> {
        return arrayListOf(
            EditorToolbarState.CropMode,
            EditorToolbarState.DrawMode,
            EditorToolbarState.TextMode,
            EditorToolbarState.EffectsMode
        )
    }
}