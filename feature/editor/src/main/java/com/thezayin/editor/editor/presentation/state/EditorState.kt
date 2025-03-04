package com.thezayin.editor.editor.presentation.state

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

@Immutable
data class EditorState(
    val currentBitmap: Bitmap? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showExitConfirmation: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)