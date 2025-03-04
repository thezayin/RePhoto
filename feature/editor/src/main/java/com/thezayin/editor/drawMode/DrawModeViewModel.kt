package com.thezayin.editor.drawMode

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.editor.drawMode.stateHandling.*
import com.thezayin.editor.drawMode.drawingCanvas.models.PathDetails
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.AbstractShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.BrushShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.LineShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.OvalShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.PenShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.RectangleShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.editor.domain.repository.ImageRepository
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class DrawModeViewModel(
    private val sessionManager: SessionManager,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // Holds the UI state
    private val _state = MutableStateFlow(DrawModeState())
    val state: StateFlow<DrawModeState> = _state.asStateFlow()

    // Channel for one-time UI events
    private val _uiEvent = Channel<DrawModeUiEvent>()
    val uiEvent: Flow<DrawModeUiEvent> = _uiEvent.receiveAsFlow()

    init {
        // Initialize by fetching the current bitmap from SessionManager
        viewModelScope.launch {
            sessionManager.getCurrentBitmap()?.let { bitmap ->
                // Initialize the bitmap stack in SessionManager
                sessionManager.initializeBitmap(bitmap)
                updateState()
            } ?: run {
                // Handle the case where no bitmap is available
                _state.value = _state.value.copy(
                    currentBitmap = null,
                    canUndo = false,
                    canRedo = false
                )
                _uiEvent.send(DrawModeUiEvent.ShowToast("No image available."))
            }
        }

        // Observe changes to baseImage URI and update the bitmap
        viewModelScope.launch {
            sessionManager.getBaseImage().collect { uri ->
                uri?.let {
                    // Fetch the bitmap from the URI
                    val bitmap = imageRepository.getBitmapFromUri(it)
                    sessionManager.initializeBitmap(bitmap)
                    updateState()
                }
            }
        }
    }

    /**
     * Handles incoming events from the UI.
     */
    fun onEvent(event: DrawModeEvent) {
        viewModelScope.launch {
            when (event) {
                is DrawModeEvent.OnUndo -> {
                    if (_state.value.canUndo) {
                        sessionManager.undo()
                        updateState()
                        _uiEvent.send(DrawModeUiEvent.ShowToast("Undo"))
                    }
                }

                is DrawModeEvent.OnRedo -> {
                    if (_state.value.canRedo) {
                        sessionManager.redo()
                        updateState()
                        _uiEvent.send(DrawModeUiEvent.ShowToast("Redo"))
                    }
                }

                is DrawModeEvent.Close -> {
                    _state.value = _state.value.copy(showExitConfirmation = true)
                }

                is DrawModeEvent.Save -> {
                    val bitmap = sessionManager.getCurrentBitmap()
                    if (bitmap != null) {
                        saveBitmapToSession(bitmap)
                    } else {
                        _uiEvent.send(DrawModeUiEvent.ShowToast("No image to save."))
                    }
                }

                is DrawModeEvent.Share -> {
                    val bitmap = sessionManager.getCurrentBitmap()
                    if (bitmap != null) {
                        _uiEvent.send(DrawModeUiEvent.ShareImage(bitmap))
                    } else {
                        _uiEvent.send(DrawModeUiEvent.ShowToast("No image to share."))
                    }
                }

                is DrawModeEvent.ConfirmExit -> {
                    resetEditor()
                }

                is DrawModeEvent.DismissExit -> {
                    _state.value = _state.value.copy(showExitConfirmation = false)
                }

                is DrawModeEvent.AddNewPath -> {
                    // Apply the path to the current bitmap and add the updated bitmap to the stack
                    applyPathToBitmap(event.pathDetail)
                }

                is DrawModeEvent.ToggleColorPicker -> {
                    _state.value = _state.value.copy(
                        showColorPicker = event.selectedColor != null || !_state.value.showColorPicker,
                        selectedColor = event.selectedColor ?: _state.value.selectedColor
                    )
                }

                is DrawModeEvent.UpdateToolbarExtensionVisibility -> {
                    _state.value = _state.value.copy(showBottomToolbarExtension = event.isVisible)
                }

                is DrawModeEvent.SelectTool -> {
                    _state.value = _state.value.copy(selectedTool = event.toolbarItem)
                    // If the selected tool is a shape tool, show the toolbar extension
                    _state.value = _state.value.copy(showBottomToolbarExtension = event.toolbarItem is BottomToolbarItemState.ShapeTool)
                }

                is DrawModeEvent.UpdateScale -> {
                    _state.value = _state.value.copy(scale = event.newScale)
                }

                is DrawModeEvent.UpdateOffset -> {
                    _state.value = _state.value.copy(offset = event.newOffset)
                }

                // Handling New Events
                is DrawModeEvent.UpdateWidth -> {
                    // Update the selected tool's width if applicable
                    val updatedTool = when (val tool = _state.value.selectedTool) {
                        is BottomToolbarItemState.BrushTool -> tool.copy(width = event.newWidth)
                        is BottomToolbarItemState.ShapeTool -> tool.copy(width = event.newWidth)
                        is BottomToolbarItemState.EraserTool -> tool.copy(width = event.newWidth)
                        else -> tool // No action for other tools
                    }
                    _state.value = _state.value.copy(selectedTool = updatedTool)
                }

                is DrawModeEvent.UpdateOpacity -> {
                    // Update the selected tool's opacity if applicable
                    val updatedTool = when (val tool = _state.value.selectedTool) {
                        is BottomToolbarItemState.BrushTool -> tool.copy(opacity = event.newOpacity)
                        is BottomToolbarItemState.ShapeTool -> tool.copy(opacity = event.newOpacity)
                        else -> tool // No action for other tools
                    }
                    _state.value = _state.value.copy(selectedTool = updatedTool)
                }

                is DrawModeEvent.UpdateShapeType -> {
                    // Update the selected tool's shape type if applicable
                    val updatedTool = when (val tool = _state.value.selectedTool) {
                        is BottomToolbarItemState.ShapeTool -> tool.copy(shapeType = event.newShapeType)
                        else -> tool // No action for other tools
                    }
                    _state.value = _state.value.copy(selectedTool = updatedTool)
                }
            }
        }
    }

    /**
     * Applies the new path to the current bitmap and updates the stack
     */
    private suspend fun applyPathToBitmap(pathDetail: PathDetails) {
        val currentBitmap = sessionManager.getCurrentBitmap()
        if (currentBitmap != null) {
            // Create a mutable copy to draw the new path
            val mutableBitmap = currentBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)

            // Safely cast drawingShape to its concrete type and invoke drawOnCanvas
            when (val shape = pathDetail.drawingShape) {
                is PenShape -> shape.drawOnCanvas(canvas)
                is BrushShape -> shape.drawOnCanvas(canvas)
                is LineShape -> shape.drawOnCanvas(canvas)
                is RectangleShape -> shape.drawOnCanvas(canvas)
                is OvalShape -> shape.drawOnCanvas(canvas)
                // Add other shape types here as needed
                else -> {
                    // Handle unknown shape types or log an error
                    // Example:
                    // Log.e("DrawModeViewModel", "Unknown shape type: ${shape::class.simpleName}")
                }
            }

            // Add the updated bitmap to the stack
            sessionManager.addBitmap(mutableBitmap)

            // Update the state
            updateState()
        }
    }

    /**
     * Updates the UI state based on the current bitmap stack.
     */
    private fun updateState() {
        _state.value = _state.value.copy(
            currentBitmap = sessionManager.getCurrentBitmap(),
            canUndo = sessionManager.canUndo(),
            canRedo = sessionManager.canRedo(),
            // Assuming scale and offset are managed by the ViewModel
            showResetZoomPanBtn = _state.value.scale != 1f || _state.value.offset != Offset.Zero
        )
    }

    /**
     * Saves the current bitmap to the SessionManager.
     */
    private suspend fun saveBitmapToSession(bitmap: Bitmap) {
        try {
            // Save the bitmap to a file
            val imgFile = File(
                imageRepository.getCacheDir(),
                "edited_image_${System.currentTimeMillis()}.jpg"
            )
            imageRepository.saveBitmap(bitmap, imgFile)

            // Get the URI for the saved file
            val uri = imageRepository.getUriForFile(imgFile)

            if (uri != null) {
                // Update the SessionManager with the new image URI
                sessionManager.setBaseImage(uri)

                // Notify the UI about the successful update
                _uiEvent.send(DrawModeUiEvent.ShowToast("Image updated successfully."))

                // Navigate back
                _uiEvent.send(DrawModeUiEvent.NavigateBack)
            } else {
                _uiEvent.send(DrawModeUiEvent.ShowToast("Failed to update image URI."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiEvent.send(DrawModeUiEvent.ShowToast("Failed to save image."))
        }
    }

    /**
     * Resets the editor state and clears the session.
     */
    private fun resetEditor() {
        _state.value = DrawModeState()
        sessionManager.clearBaseImage()
        viewModelScope.launch {
            _uiEvent.send(DrawModeUiEvent.ShowToast("Editor reset."))
            _uiEvent.send(DrawModeUiEvent.NavigateBack)
        }
    }

    /**
     * Resets zoom and pan to default values.
     */
    fun resetZoomAndPan() {
        onEvent(DrawModeEvent.UpdateScale(1f))
        onEvent(DrawModeEvent.UpdateOffset(Offset.Zero))
    }
}
