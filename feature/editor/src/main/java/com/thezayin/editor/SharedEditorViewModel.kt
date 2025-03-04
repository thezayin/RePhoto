package com.thezayin.editor

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.thezayin.editor.editor.presentation.state.EditorState
import com.thezayin.framework.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Stack

class SharedEditorViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    var useTransition = false
    var bitmapStack = Stack<Bitmap>()
        private set
    var bitmapRedoStack = Stack<Bitmap>()
        private set

    private val _recompositionTrigger = MutableStateFlow<Long>(0)
    val recompositionTrigger: StateFlow<Long> = _recompositionTrigger

    private var latestTimeForAddingBitmapToStack: Long = 0

    /**
     * Call this function after making sure that the bitmapStack won't be empty
     */
    @Throws(Exception::class)
    fun getCurrentBitmap(): Bitmap {
        if (bitmapStack.isEmpty()) {
            throw Exception("EmptyStackException: The bitmapStack should contain at least one bitmap")
        }
        return bitmapStack.peek()
    }

    fun resetStacks() {
        bitmapStack.clear()
        bitmapRedoStack.clear()
        sessionManager.clearBaseImage()
    }

    fun addBitmapToStack(
        bitmap: Bitmap,
        triggerRecomposition: Boolean = false,
        addSafelyWithoutMultipleTriggers: Boolean = true
    ) {
        val currTime = System.currentTimeMillis()
        if (addSafelyWithoutMultipleTriggers) {
            val timeDiff = currTime - latestTimeForAddingBitmapToStack
            if (timeDiff < 1000) {
                return
            }
        }
        latestTimeForAddingBitmapToStack = currTime

        bitmapStack.push(bitmap)
        bitmapRedoStack.clear()
        // Note: Conversion from Bitmap to Uri should be handled in the Composable layer
        if (triggerRecomposition) {
            // Trigger recomposition while adding initialBitmap in MainScreen
            _recompositionTrigger.update { recompositionTrigger.value + 1 }
        }
    }

    fun updateStacksFromEditorState(finalEditorState: EditorState) {
//        bitmapStack = finalEditorState.bitmapStack
//        bitmapRedoStack = finalEditorState.bitmapRedoStack
    }
}
