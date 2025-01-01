package com.thezayin.background_changer.action

import android.graphics.Bitmap
import android.net.Uri

sealed class ChangerIntent {
    object LoadInitialData : ChangerIntent()
    data class SelectFromGallery(val uri: Uri) : ChangerIntent()
    data class SelectDrawableImage(val drawableBitmap: Bitmap) : ChangerIntent()
    data class SelectCategoryImage(val category: String, val imageUrl: String) : ChangerIntent()
    object MoreOptions : ChangerIntent()
    object ResetBackground : ChangerIntent()
    object ShowBaseImage : ChangerIntent()
    object HideBaseImage : ChangerIntent()
    data class ChangeSmoothing(val smoothingValue: Float) : ChangerIntent()
    object ApplyChanges : ChangerIntent()
    object NavigateToNextScreen : ChangerIntent()
}
