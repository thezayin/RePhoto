package com.thezayin.framework.session

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack

class EditingSessionManagerImpl(
    private val context: Context,
    private val sessionManager: SessionManager,
) : EditingSessionManager {

    private val _bitmapStack = MutableStateFlow(Stack<Bitmap>())
    private val _bitmapRedoStack = MutableStateFlow(Stack<Bitmap>())

    override fun getBitmapStack(): StateFlow<Stack<Bitmap>> = _bitmapStack.asStateFlow()
    override fun getRedoStack(): StateFlow<Stack<Bitmap>> = _bitmapRedoStack.asStateFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun startObservingBaseImage() {
        scope.launch {
            sessionManager.getBaseImage().collect { uri ->
                if (uri != null) {
                    // Offload decode to background
                    val decodedBitmap = withContext(Dispatchers.IO) {
                        // This function decodes and optionally scales the image
                        loadScaledBitmapFromUri(uri, maxWidth = 2000, maxHeight = 2000)
                    }
                    decodedBitmap?.let {
                        pushBitmap(it)
                    }
                }
            }
        }
    }


    /**
     * Loads and *optionally* scales down a Bitmap from the given Uri on IO thread.
     * @param maxWidth & maxHeight If the image is larger than these, it will be downscaled.
     */
    private suspend fun loadScaledBitmapFromUri(uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                // 1) Read dimensions only
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)

                val (actualW, actualH) = options.outWidth to options.outHeight
                if (actualW <= 0 || actualH <= 0) {
                    return@use null // invalid image
                }

                // 2) Calculate a sampleSize to scale down
                val sampleSize = calculateInSampleSize(actualW, actualH, maxWidth, maxHeight)

                // 3) Decode actual with sampleSize
                val decodeOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inSampleSize = sampleSize
                }

                // We need a fresh InputStream because the first is exhausted
                context.contentResolver.openInputStream(uri).use { secondStream ->
                    BitmapFactory.decodeStream(secondStream, null, decodeOptions)
                }
            }
        }
    }

    /**
     * Basic logic to compute how much we can scale down the image while preserving aspect ratio.
     */
    private fun calculateInSampleSize(
        width: Int, height: Int,
        maxWidth: Int, maxHeight: Int
    ): Int {
        var inSampleSize = 1
        if (height > maxHeight || width > maxWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= maxHeight && (halfWidth / inSampleSize) >= maxWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    override fun pushBitmap(bitmap: Bitmap) {
        val mainStack = _bitmapStack.value
        mainStack.push(bitmap)
        _bitmapRedoStack.value.clear()
        _bitmapStack.value = mainStack
        Log.d("EditingSessionManager", "pushBitmap: stackSize=${mainStack.size}")
    }

    override fun popBitmapToRedo() {
        val mainStack = _bitmapStack.value
        if (mainStack.isNotEmpty()) {
            val redoStack = _bitmapRedoStack.value
            redoStack.push(mainStack.pop())
            _bitmapStack.value = mainStack
            _bitmapRedoStack.value = redoStack
        }
    }

    override fun popRedoToBitmap() {
        val redoStack = _bitmapRedoStack.value
        if (redoStack.isNotEmpty()) {
            val mainStack = _bitmapStack.value
            mainStack.push(redoStack.pop())
            _bitmapStack.value = mainStack
            _bitmapRedoStack.value = redoStack
        }
    }

    override fun clearAll() {
        _bitmapStack.value.clear()
        _bitmapRedoStack.value.clear()
    }
}
