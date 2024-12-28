package com.thezayin.presentation.component

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thezayin.presentation.state.GalleryState
import ir.kaaveh.sdpcompose.sdp

@Composable
fun GalleryScreenContent(
    onImageSelected: (Uri) -> Unit,
    onAlbumSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    state: GalleryState,
) {
    Scaffold(
        topBar = {
            TopBar(
                albumsState = state,
                onBackClick = onBackClick,
                onCameraClick = onCameraClick,
                onAlbumSelected = onAlbumSelected

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ImageGrid(
                images = state.images,
                onImageClick = { image ->
                    onImageSelected(image)
                }
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.sdp))
                Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}