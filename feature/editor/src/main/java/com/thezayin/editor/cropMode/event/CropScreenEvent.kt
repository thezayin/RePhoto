package com.thezayin.editor.cropMode.event

import android.graphics.Bitmap


sealed class CropScreenEvent {
    data object StartCropping : CropScreenEvent()
    data class CropCompleted(val croppedBitmap: Bitmap) : CropScreenEvent()
    data object CropCancelled : CropScreenEvent()
}