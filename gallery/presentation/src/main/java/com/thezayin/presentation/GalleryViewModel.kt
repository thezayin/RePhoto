package com.thezayin.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.domain.usecase.AlbumsUseCase
import com.thezayin.domain.usecase.ImagesUseCase
import com.thezayin.framework.session.EditingSessionManager
import com.thezayin.framework.session.SessionManager
import com.thezayin.framework.utils.Response
import com.thezayin.presentation.event.GalleryEvent
import com.thezayin.presentation.state.GalleryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val albumsUseCase: AlbumsUseCase,
    private val imagesUseCase: ImagesUseCase,
    private val sessionManager: SessionManager,
    private val editingSessionManager: EditingSessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state.asStateFlow()

    fun sendEvent(event: GalleryEvent) {
        viewModelScope.launch {
            when (event) {
                is GalleryEvent.LoadAlbums -> loadAlbums()
                is GalleryEvent.SelectAlbum -> selectAlbum(event.albumId)
                is GalleryEvent.SelectImage -> selectImage(event.imageUri)
                is GalleryEvent.ResetImageSelection -> resetImageSelection()
            }
        }
    }

    private suspend fun loadAlbums() {
        _state.update { it.copy(isLoadingAlbums = true, errorMessage = null) }
        albumsUseCase().collect { response ->
            when (response) {
                is Response.Loading -> {
                    _state.update { it.copy(isLoadingAlbums = true) }
                }

                is Response.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingAlbums = false,
                            albums = response.data,
                            selectedAlbum = response.data.firstOrNull()
                        )
                    }
                    // Automatically load images for the first album
                    response.data.firstOrNull()?.let { firstAlbum ->
                        sendEvent(GalleryEvent.SelectAlbum(firstAlbum.id))
                    }
                }

                is Response.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingAlbums = false,
                            errorMessage = response.e
                        )
                    }
                }
            }
        }
    }

    private suspend fun selectAlbum(albumId: String) {
        _state.update { it.copy(isLoadingImages = true, errorMessage = null) }
        imagesUseCase(albumId).collect { response ->
            when (response) {
                is Response.Loading -> {
                    _state.update { it.copy(isLoadingImages = true) }
                }

                is Response.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingImages = false,
                            images = response.data,
                            selectedAlbum = currentState.albums.find { it.id == albumId }
                        )
                    }
                }

                is Response.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingImages = false,
                            errorMessage = response.e
                        )
                    }
                }
            }
        }
    }

    private fun selectImage(uri: Uri) {
        viewModelScope.launch {
            sessionManager.setBaseImage(uri)
            _state.update { it.copy(isImageSelected = true) }
        }
    }

    private fun resetImageSelection() {
        viewModelScope.launch {
            _state.update { it.copy(isImageSelected = false) }
        }
    }
}
