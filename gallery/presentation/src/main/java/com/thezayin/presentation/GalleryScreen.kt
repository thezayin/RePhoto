package com.thezayin.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.thezayin.framework.extension.createImageFile
import com.thezayin.presentation.component.GalleryPermissionHandler
import com.thezayin.presentation.component.GalleryScreenContent
import com.thezayin.presentation.event.GalleryEvent
import org.koin.compose.koinInject
import java.io.File

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = koinInject(),
    onNavigateToNextScreen: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val photoFile: File by remember {
        mutableStateOf(createImageFile(context))
    }
    val photoUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.sendEvent(GalleryEvent.SelectImage(photoUri))
            Toast.makeText(context, "Image saved to $photoUri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Picture not taken or canceled", Toast.LENGTH_SHORT).show()
        }
    }

    GalleryPermissionHandler(
        onPermissionGranted = {
            LaunchedEffect(Unit) { viewModel.sendEvent(GalleryEvent.LoadAlbums) }
            GalleryScreenContent(
                onImageSelected = { uri: Uri ->
                    viewModel.sendEvent(GalleryEvent.SelectImage(uri))
                    Toast.makeText(context, "Image saved to $uri", Toast.LENGTH_SHORT).show()
                },
                onAlbumSelected = { albumId: String ->
                    viewModel.sendEvent(GalleryEvent.SelectAlbum(albumId))
                },
                onCameraClick = {
                    takePictureLauncher.launch(photoUri)
                },
                state = state,
                onBackClick = navigateBack,
            )
        }
    )

    if (state.isImageSelected) {
        LaunchedEffect(Unit) {
            onNavigateToNextScreen()
            viewModel.sendEvent(GalleryEvent.ResetImageSelection)
        }
    }
}