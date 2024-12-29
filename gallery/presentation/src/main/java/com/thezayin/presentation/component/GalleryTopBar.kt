package com.thezayin.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.thezayin.domain.model.Album
import com.thezayin.presentation.state.GalleryState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun AlbumDropdownItem(
    album: Album,
    onAlbumSelected: (String) -> Unit
) {
    DropdownMenuItem(
        colors = MenuItemColors(
            textColor = colorResource(R.color.white),
            trailingIconColor = colorResource(R.color.white),
            disabledTextColor = colorResource(R.color.white),
            disabledTrailingIconColor = colorResource(R.color.white),
            leadingIconColor = colorResource(R.color.white),
            disabledLeadingIconColor = colorResource(R.color.white),
        ),
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(album.coverImageUri),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .size(24.sdp)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.sdp))
                Text(text = album.name, color = colorResource(R.color.white))
            }
        },
        onClick = {
            onAlbumSelected(album.id)
        }
    )
}

// TopBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    albumsState: GalleryState,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onAlbumSelected: (String) -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = colorResource(R.color.black),
        ),
        title = {
            when {
                albumsState.isLoadingAlbums -> {
                    Text(text = "Loading Albums...")
                }

                albumsState.albums.isNotEmpty() -> {
                    AlbumDropdownMenu(
                        albums = albumsState.albums,
                        selectedAlbum = albumsState.selectedAlbum,
                        onAlbumSelected = onAlbumSelected
                    )
                }

                albumsState.errorMessage != null -> {
                    Text(text = "Error Loading Albums", color = Color.Red)
                }

                else -> {
                    Text(text = "No Albums Available", color = colorResource(R.color.white))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(R.color.white)
                )
            }
        },
        actions = {
            IconButton(onClick = onCameraClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Camera",
                    tint = colorResource(R.color.white)
                )
            }
        },
    )
}

// AlbumDropdownMenu.kt
@Composable
fun AlbumDropdownMenu(
    albums: List<Album>,
    selectedAlbum: Album?,
    onAlbumSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    selectedAlbum?.let { album ->
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(end = 8.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(album.coverImageUri),
                contentDescription = "Album Cover",
                modifier = Modifier
                    .size(24.sdp)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.sdp))
            Text(text = album.name, color = colorResource(R.color.white))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = colorResource(R.color.white)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            albums.forEach { album ->
                AlbumDropdownItem(
                    album = album,
                    onAlbumSelected = { selectedId ->
                        onAlbumSelected(selectedId)
                        expanded = false
                    }
                )
            }
        }
    }
}
