package com.thezayin.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun SmsPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permission = Manifest.permission.SEND_SMS
    val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            }
        }
    )

    if (!hasPermission) {
        LaunchedEffect(key1 = Unit) {
            launcher.launch(permission)
        }

        // Show rationale dialog
        AlertDialog(
            onDismissRequest = { /* Optionally handle dismiss */ },
            title = { Text("SMS Permission Required") },
            text = { Text("This app requires SMS permission to send birthday messages automatically.") },
            confirmButton = {
                TextButton(onClick = { launcher.launch(permission) }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { /* Optionally handle denial */ }) {
                    Text("Deny")
                }
            }
        )
    } else {
        onPermissionGranted()
    }
}
