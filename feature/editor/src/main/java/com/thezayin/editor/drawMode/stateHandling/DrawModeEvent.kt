package com.thezayin.editor.drawMode.stateHandling

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.drawMode.drawingCanvas.models.PathDetails
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState

sealed class DrawModeEvent {
    // Drawing actions
    data class AddNewPath(val pathDetail: PathDetails) : DrawModeEvent()
    data class ToggleColorPicker(val selectedColor: Color?) : DrawModeEvent()

    // Toolbar visibility
    data class UpdateToolbarExtensionVisibility(val isVisible: Boolean) : DrawModeEvent()

    // Tool selection
    data class SelectTool(val toolbarItem: BottomToolbarItemState) : DrawModeEvent()

    // Undo and Redo actions
    object OnUndo : DrawModeEvent()
    object OnRedo : DrawModeEvent()

    // Save and Share actions
    object Save : DrawModeEvent()
    object Share : DrawModeEvent()

    // Exit confirmation
    object ConfirmExit : DrawModeEvent()
    object DismissExit : DrawModeEvent()
    object Close : DrawModeEvent()

    // Zoom and Pan actions
    data class UpdateScale(val newScale: Float) : DrawModeEvent()
    data class UpdateOffset(val newOffset: Offset) : DrawModeEvent()

    // New Events for Toolbar Extension
    data class UpdateWidth(val newWidth: Float) : DrawModeEvent()
    data class UpdateOpacity(val newOpacity: Float) : DrawModeEvent()
    data class UpdateShapeType(val newShapeType: ShapeType) : DrawModeEvent()
}

sealed class DrawModeUiEvent {
    data class ShowToast(val message: String) : DrawModeUiEvent()
    object NavigateBack : DrawModeUiEvent()
    data class ShareImage(val bitmap: Bitmap) : DrawModeUiEvent()
    data class SaveImage(val bitmap: Bitmap) : DrawModeUiEvent()
}