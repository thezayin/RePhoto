package com.thezayin.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.kaaveh.sdpcompose.sdp

@Composable
fun PermissionDeniedComposable(
    onRequestPermission: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gallery access is required to display images.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.sdp)
            )
            Button(onClick = onRequestPermission) {
                Text(text = "Allow Images")
            }
        }
    }
}
