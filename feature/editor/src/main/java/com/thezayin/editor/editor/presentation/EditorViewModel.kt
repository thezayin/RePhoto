// File: com/thezayin.editor.editorScreen/presentation/EditorScreenViewModel.kt

package com.thezayin.editor.editor.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.editor.editor.domain.repository.ImageRepository
import com.thezayin.editor.editor.presentation.event.EditorEvent
import com.thezayin.editor.editor.presentation.event.EditorUiEvent
import com.thezayin.editor.editor.presentation.state.EditorState
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditorViewModel(
    private val sessionManager: SessionManager,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // Holds the UI state
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    // Channel for one-time UI events
    private val _uiEvent = Channel<EditorUiEvent>()
    val uiEvent: Flow<EditorUiEvent> = _uiEvent.receiveAsFlow()

    private var baseImageUri: Uri? = null

    init {
        // Collect the base image URI and initialize the bitmap stack
        viewModelScope.launch {
            sessionManager.getBaseImage().collectLatest { uri ->
                if (uri != null) {
                    baseImageUri = uri
                    try {
                        val bitmap = imageRepository.getBitmapFromUri(uri)
                        sessionManager.initializeBitmap(bitmap)
                        updateState()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load image."
                        )
                        _uiEvent.send(EditorUiEvent.ShowToast("Failed to load image."))
                    }
                } else {
                    _state.value = _state.value.copy(
                        currentBitmap = null,
                        canUndo = false,
                        canRedo = false,
                        isLoading = false,
                        errorMessage = "No image selected."
                    )
                    _uiEvent.send(EditorUiEvent.ShowToast("No image selected."))
                }
            }
        }
    }

    /**
     * Handles incoming events from the UI.
     */
    fun onEvent(event: EditorEvent) {
        viewModelScope.launch {
            when (event) {
                is EditorEvent.Undo -> {
                    if (_state.value.canUndo) {
                        sessionManager.undo()
                        updateState()
                        _uiEvent.send(EditorUiEvent.ShowToast("Undo"))
                    }
                }

                is EditorEvent.Redo -> {
                    if (_state.value.canRedo) {
                        sessionManager.redo()
                        updateState()
                        _uiEvent.send(EditorUiEvent.ShowToast("Redo"))
                    }
                }

                is EditorEvent.Close -> {
                    _state.value = _state.value.copy(showExitConfirmation = true)
                }

                is EditorEvent.Save -> {
                    _state.value.currentBitmap?.let { bitmap ->
                        _uiEvent.send(EditorUiEvent.SaveImage(bitmap))
                    } ?: run {
                        _uiEvent.send(EditorUiEvent.ShowToast("No image to save."))
                    }
                }

                is EditorEvent.Share -> {
                    _state.value.currentBitmap?.let { bitmap ->
                        _uiEvent.send(EditorUiEvent.ShareImage(bitmap))
                    } ?: run {
                        _uiEvent.send(EditorUiEvent.ShowToast("No image to share."))
                    }
                }

                is EditorEvent.ConfirmExit -> {
                    resetEditor()
                }

                is EditorEvent.DismissExit -> {
                    _state.value = _state.value.copy(showExitConfirmation = false)
                }
            }
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
            isLoading = false,
            errorMessage = null
        )
    }

    /**
     * Resets the editor state and clears the session.
     */
    private fun resetEditor() {
        _state.value = EditorState()
        sessionManager.clearBaseImage()
        viewModelScope.launch {
            _uiEvent.send(EditorUiEvent.ShowToast("Editor reset."))
        }
    }
}
