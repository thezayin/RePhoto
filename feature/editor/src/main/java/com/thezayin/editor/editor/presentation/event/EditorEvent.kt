package com.thezayin.editor.editor.presentation.event

import android.graphics.Bitmap
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.drawMode.stateHandling.DrawModeEvent
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.editor.presentation.state.EditorToolbarState

sealed class EditorEvent {
    object Undo : EditorEvent()
    object Redo : EditorEvent()
    object Close : EditorEvent()
    object Save : EditorEvent()
    object Share : EditorEvent()
    object ConfirmExit : EditorEvent()
    object DismissExit : EditorEvent()
}

sealed class EditorUiEvent {
    data class ShowToast(val message: String) : EditorUiEvent()
    data class ShareImage(val bitmap: Bitmap) : EditorUiEvent()
    data class SaveImage(val bitmap: Bitmap) : EditorUiEvent()
}

sealed class EditorBottomBarEvent {
    data class OnItemClicked(val toolbarItem: EditorToolbarState) : EditorBottomBarEvent()
}

sealed class BottomToolbarEvent {
    data class OnItemClicked(val toolbarItem: BottomToolbarItemState) : BottomToolbarEvent()
    data class UpdateWidth(val newWidth: Float) : BottomToolbarEvent()
    data class UpdateOpacity(val newOpacity: Float) : BottomToolbarEvent()
    data class UpdateShapeType(val newShapeType: ShapeType) : BottomToolbarEvent()
}

fun BottomToolbarEvent.toDrawModeEvent(): DrawModeEvent {
    return when (this) {
        is BottomToolbarEvent.OnItemClicked -> {
            DrawModeEvent.SelectTool(this.toolbarItem)
        }

        is BottomToolbarEvent.UpdateOpacity -> {
            DrawModeEvent.UpdateOpacity(this.newOpacity)
        }

        is BottomToolbarEvent.UpdateShapeType -> {
            DrawModeEvent.UpdateShapeType(this.newShapeType)
        }

        is BottomToolbarEvent.UpdateWidth -> {
            DrawModeEvent.UpdateWidth(this.newWidth)
        }
    }
}