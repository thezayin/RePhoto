package com.thezayin.editor.editor.presentation.state

sealed class EditorToolbarState {
    data object CropMode : EditorToolbarState()
    data object DrawMode : EditorToolbarState()
    data object TextMode : EditorToolbarState()
    data object EffectsMode : EditorToolbarState()
}