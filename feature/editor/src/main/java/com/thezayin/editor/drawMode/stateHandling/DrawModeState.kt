package com.thezayin.editor.drawMode.stateHandling

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.drawMode.drawingCanvas.models.PathDetails
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import java.util.Stack

data class DrawModeState(
    val showColorPicker: Boolean = false,
    val selectedColor: Color = Color.White,
    val selectedTool: BottomToolbarItemState = BottomToolbarItemState.NONE,
    val showBottomToolbarExtension: Boolean = false,
    val pathDetailList: List<PathDetails> = emptyList(),
    val redoPathDetailList: List<PathDetails> = emptyList(),
    val currentBitmap: Bitmap? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showResetZoomPanBtn: Boolean = false,
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val showExitConfirmation: Boolean = false,
    val selectedShapeType: ShapeType? = null,
)