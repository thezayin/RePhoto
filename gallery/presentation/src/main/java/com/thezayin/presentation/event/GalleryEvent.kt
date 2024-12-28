package com.thezayin.presentation.event

import android.net.Uri

sealed class GalleryEvent {
    data object LoadAlbums : GalleryEvent()
    data class SelectAlbum(val albumId: String) : GalleryEvent()
    data class SelectImage(val imageUri: Uri) : GalleryEvent()
    data object ResetImageSelection : GalleryEvent()
}