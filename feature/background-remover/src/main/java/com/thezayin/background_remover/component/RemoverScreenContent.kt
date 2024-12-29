package com.thezayin.background_remover.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thezayin.background_remover.state.BackgroundRemoverState

@Composable
fun RemoverScreenContent(
    showSaveDialog: Boolean,
    onDismissRequest: (Boolean) -> Unit,
    saveAsPng: (Boolean) -> Unit,
    state: BackgroundRemoverState,
    onBack: () -> Unit,
    onDownloadClick: () -> Unit,
    adjustSmoothing: (Int) -> Unit,
) {

    if (showSaveDialog) {
        RemoverSaveDialog(
            onDismissRequest = onDismissRequest,
            saveAsPng = saveAsPng
        )
    }

    val isDownloadEnabled =
        !state.isLoading && state.displayBitmap != null && state.errorMessage == null

    Scaffold(
        containerColor = colorResource(com.thezayin.values.R.color.black),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            RemoverTopBar(
                onBackClicked = { onBack() },
                onDownloadClicked = onDownloadClick,
                isDownloadEnabled = isDownloadEnabled
            )
        },
        bottomBar = {
            if (!state.isLoading && state.displayBitmap != null) {
                RemoverSmoothingSlider(
                    state = state,
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
            if (!state.isLoading && state.originalBitmap != null && state.displayBitmap != null) {
                RemoverBeforeAfter(state = state)
            }
            if (state.isLoading) {
                RemovingDialog(
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