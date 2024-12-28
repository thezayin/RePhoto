package com.thezayin.presentation.component

import android.Manifest
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import com.thezayin.framework.extension.openAppSettings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryPermissionHandler(
    onPermissionGranted: @Composable () -> Unit
) {
    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)

    LaunchedEffect(key1 = Unit) {
        permissionState.launchPermissionRequest()
    }

    when {
        permissionState.status.isGranted -> {
            onPermissionGranted()
        }

        permissionState.status.shouldShowRationale -> {
            AlertDialog(
                onDismissRequest = { /* Do nothing to force the user to respond */ },
                title = { Text(text = "Permission Required") },
                text = { Text(text = "This app needs access to your gallery to display images.") },
                confirmButton = {
                    TextButton(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Allow Images")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { /* Optional: Handle dismiss action */ }) {
                        Text("Cancel")
                    }
                },
                properties = DialogProperties(dismissOnClickOutside = false)
            )
        }

        !permissionState.status.isGranted && !permissionState.status.shouldShowRationale -> {
            PermissionDeniedComposable(
                onRequestPermission = {
                    openAppSettings(context)
                }
            )
        }
    }
}
