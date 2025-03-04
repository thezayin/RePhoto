package com.thezayin.framework.session

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Stack

class SessionManagerImpl : SessionManager {
    private val _baseImage = MutableStateFlow<Uri?>(null)
    override fun getBaseImage(): StateFlow<Uri?> = _baseImage

    override fun setBaseImage(uri: Uri) {
        _baseImage.value = uri
    }

    override fun clearBaseImage() {
        _baseImage.value = null
        // Clear the bitmap stacks when clearing the base image
        bitmapStack.clear()
        bitmapRedoStack.clear()
    }

    // Initialize bitmap stacks
    private val bitmapStack = Stack<Bitmap>()
    private val bitmapRedoStack = Stack<Bitmap>()

    override fun undo() {
        if (canUndo()) { // Ensure there's something to undo
            val poppedBitmap = bitmapStack.pop()
            bitmapRedoStack.push(poppedBitmap)
        }
        // Else, do nothing or handle as per your requirement
    }

    override fun redo() {
        if (canRedo()) {
            val redoneBitmap = bitmapRedoStack.pop()
            bitmapStack.push(redoneBitmap)
        }
        // Else, do nothing or handle as per your requirement
    }

    override fun getCurrentBitmap(): Bitmap? {
        return if (bitmapStack.isNotEmpty()) {
            Log.d("SessionManagerImpl", "Bitmap available")
            bitmapStack.peek()

        } else {
            Log.d("SessionManagerImpl", "No bitmap available")
            null
        }
    }

    override fun canUndo(): Boolean {
        return bitmapStack.size > 1
    }

    override fun canRedo(): Boolean {
        return bitmapRedoStack.isNotEmpty()
    }

    override fun initializeBitmap(bitmap: Bitmap) {
        if (bitmapStack.isEmpty()) {
            bitmapStack.push(bitmap)
            // Clear any redo history
            bitmapRedoStack.clear()
        }
    }

    override fun addBitmap(bitmap: Bitmap) {
        bitmapStack.push(bitmap)
        // Clear the redo stack whenever a new action is performed
        bitmapRedoStack.clear()
    }
}
