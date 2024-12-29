package com.thezayin.background_remover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.thezayin.background_remover.component.RemoverScreenContent
import com.thezayin.background_remover.event.BackgroundRemoverEvent
import org.koin.compose.koinInject

@Composable
fun BackgroundRemoverScreen(
    onBack: () -> Unit,
    viewModel: BackgroundRemoverViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }

    RemoverScreenContent(
        onBack = onBack,
        state = state,
        showSaveDialog = showSaveDialog,
        onDismissRequest = {
            showSaveDialog = it
        },
        saveAsPng = { isPng ->
            viewModel.handleEvent(BackgroundRemoverEvent.SaveImage(isPng))
        },
        onDownloadClick = {
            showSaveDialog = true
        },
        adjustSmoothing = { value ->
            viewModel.handleEvent(BackgroundRemoverEvent.AdjustSmoothing(value))
        }
    )
}