package com.thezayin.background_blur

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.thezayin.background_blur.component.BlurScreenContent
import com.thezayin.background_blur.event.BackgroundBlurEvent
import org.koin.compose.koinInject

/**
 * Composable that represents the Background Blur screen, integrating permission handling.
 *
 * @param onBack Callback invoked when the back button is pressed.
 * @param viewModel The ViewModel instance for Background Blur.
 */
@Composable
fun BackgroundBlurScreen(
    onBack: () -> Unit,
    viewModel: BackgroundBlurViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    BlurScreenContent(
        onBack = onBack,
        state = state,
        showSaveDialog = showSaveDialog,
        onDismissRequest = { showSaveDialog = it },
        saveAsPng = { isPng ->
            viewModel.handleEvent(
                BackgroundBlurEvent.SaveImage(
                    filename = "blurred_image_${System.currentTimeMillis()}",
                    asPng = isPng
                )
            )
        },
        onDownloadClick = {
            showSaveDialog = true
        },
        adjustBlurIntensity = { value ->
            viewModel.handleEvent(BackgroundBlurEvent.UpdateBlurIntensity(value))
        },
        adjustSmoothing = { value ->
            viewModel.handleEvent(BackgroundBlurEvent.UpdateEdgeSmoothness(value))
        }
    )
}
