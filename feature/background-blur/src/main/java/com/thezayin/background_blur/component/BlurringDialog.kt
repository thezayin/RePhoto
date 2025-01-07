package com.thezayin.background_blur.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thezayin.values.R

/**
 * Loading dialog displayed during image processing tasks.
 *
 * @param modifier Modifier for styling.
 * @param statusText Current status message to display.
 */
@Composable
fun BlurringDialog(
    modifier: Modifier = Modifier,
    statusText: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = colorResource(R.color.button_color))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = statusText,
            color = colorResource(R.color.white),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
