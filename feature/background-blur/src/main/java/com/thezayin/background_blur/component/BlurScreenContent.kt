package com.thezayin.background_blur.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.background_blur.state.BackgroundBlurState
import com.thezayin.values.R

@Composable
fun BlurScreenContent(
    showSaveDialog: Boolean,
    onDismissRequest: (Boolean) -> Unit,
    saveAsPng: (Boolean) -> Unit,
    state: BackgroundBlurState,
    onBack: () -> Unit,
    onDownloadClick: () -> Unit,
    adjustBlurIntensity: (Int) -> Unit,
    adjustSmoothing: (Int) -> Unit,
) {
    if (showSaveDialog) {
        BlurSaveDialog(
            onDismissRequest = onDismissRequest,
            saveAsPng = saveAsPng
        )
    }

    val isDownloadEnabled =
        !state.isLoading && state.displayBitmap != null && state.errorMessage == null

    Scaffold(
        containerColor = colorResource(R.color.black),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            BlurTopBar(
                onBackClicked = { onBack() },
                onDownloadClicked = onDownloadClick,
                isDownloadEnabled = isDownloadEnabled
            )
        },
        bottomBar = {
            if (!state.isLoading && state.displayBitmap != null) {
                BlurMenuSliders(
                    state = state,
                    adjustBlurIntensity = adjustBlurIntensity,
                    adjustSmoothing = adjustSmoothing
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (!state.isLoading && state.displayBitmap != null && state.errorMessage == null) {
                var showOriginal by remember { mutableStateOf(false) }
                Image(
                    bitmap = if (showOriginal) state.originalBitmap?.asImageBitmap()
                        ?: state.displayBitmap.asImageBitmap() else state.displayBitmap.asImageBitmap(),
                    contentDescription = stringResource(id = R.string.content_description_processed_image),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )

                BlurCompareIcon(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onPressStart = {
                        showOriginal = true
                    },
                    onPressEnd = {
                        showOriginal = false
                    }
                )
            }
            if (state.isLoading) {
                BlurringDialog(
                    modifier = Modifier,
                    statusText = state.statusText
                )
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}