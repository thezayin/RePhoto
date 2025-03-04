package com.thezayin.framework.session

import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun setBaseImage(uri: Uri)
    fun getBaseImage(): Flow<Uri?>
    fun clearBaseImage()
    // Undo and redo functions
    fun undo()
    fun redo()
    fun getCurrentBitmap(): Bitmap?

    // Newly added functions for bitmap stack management
    fun initializeBitmap(bitmap: Bitmap)
    fun addBitmap(bitmap: Bitmap)

    // Functions to check if undo and redo are possible
    fun canUndo(): Boolean
    fun canRedo(): Boolean
}