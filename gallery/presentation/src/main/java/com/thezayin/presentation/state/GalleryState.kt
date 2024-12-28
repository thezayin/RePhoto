package com.thezayin.presentation.state

import com.thezayin.domain.model.Album
import com.thezayin.domain.model.Image

data class GalleryState(
    val isLoadingAlbums: Boolean = false,
    val albums: List<Album> = emptyList(),
    val selectedAlbum: Album? = null,
    val isLoadingImages: Boolean = false,
    val images: List<Image> = emptyList(),
    val isImageSelected: Boolean = false,
    val errorMessage: String? = null
)