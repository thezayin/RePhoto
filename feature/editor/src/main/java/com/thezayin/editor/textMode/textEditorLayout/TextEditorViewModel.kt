package com.thezayin.editor.textMode.textEditorLayout

import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class TextEditorViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _editorState = MutableStateFlow(
        TextEditorState(
            textStateId = UUID.randomUUID().toString(),
            textFont = 0.sp
        )
    )
    val editorState: StateFlow<TextEditorState> = _editorState

    fun updateInitialState(initialState: TextEditorState) {
        _editorState.update { initialState }
    }

    fun onEvent(event: TextEditorEvent) = viewModelScope.launch {
        when (event) {

            is TextEditorEvent.SelectTextColor -> {
                _editorState.update { it.copy(selectedColor = event.color) }
            }

            is TextEditorEvent.SelectTextAlign -> {
                _editorState.update { it.copy(textAlign = event.textAlign) }
            }

            is TextEditorEvent.UpdateTextFieldValue -> {
                _editorState.update { it.copy(text = event.textInput) }
            }

        }
    }
}