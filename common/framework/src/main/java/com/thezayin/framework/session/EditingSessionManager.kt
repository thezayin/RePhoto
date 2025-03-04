package com.thezayin.framework.session

import android.graphics.Bitmap
import kotlinx.coroutines.flow.StateFlow
import java.util.Stack

interface EditingSessionManager {
    /**
     * Observes and loads the base image from the underlying SessionManager (URI-based).
     * Then transforms it into a Bitmap in the internal stack, if needed.
     */
    fun startObservingBaseImage()

    // Expose the current main (undo) stack & redo stack
    fun getBitmapStack(): StateFlow<Stack<Bitmap>>
    fun getRedoStack(): StateFlow<Stack<Bitmap>>

    // Basic editing operations
    fun pushBitmap(bitmap: Bitmap)
    fun popBitmapToRedo()
    fun popRedoToBitmap()

    // Clears all editing data
    fun clearAll()
}
