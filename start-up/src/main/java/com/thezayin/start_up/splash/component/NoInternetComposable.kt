package com.thezayin.start_up.splash.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun NoInternetComposable(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No internet connection. Please check your connection.",
            color = colorResource(R.color.text_color),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.sdp)
        )
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}
