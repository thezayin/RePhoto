// BackgroundChangerScreen.kt
package com.thezayin.background_changer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thezayin.background_changer.action.ChangerIntent
import com.thezayin.background_changer.compnent.BGScreenContent
import org.koin.compose.koinInject

@Composable
fun BackgroundChangerScreen(
    viewModel: BackgroundChangerViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.handleIntent(ChangerIntent.SelectFromGallery(it))
            }
        }
    )

    BGScreenContent(
        state = state,
        setDrawableBackground = { drawableBitmap ->
            viewModel.handleIntent(ChangerIntent.SelectDrawableImage(drawableBitmap))
        },
        onSelectedCategory = { imageRes ->
            viewModel.handleIntent(
                ChangerIntent.SelectCategoryImage(
                    category = "",
                    imageUrl = imageRes
                )
            )
        },
        onCompareLongPressStart = {
            viewModel.handleIntent(ChangerIntent.ShowBaseImage)
        },
        onCompareLongPressEnd = {
            viewModel.handleIntent(ChangerIntent.HideBaseImage)
        },
        onImageScaleChange = { newScale ->
            viewModel.updateImageScale(newScale)
        },
        onImageOffsetChange = { newX, newY ->
            viewModel.updateImageOffset(newX, newY)
        },
        onImageRotationChange = { newRotation ->
            viewModel.updateImageRotation(newRotation)
        },
        applySmoothing = { newValue ->
            viewModel.handleIntent(ChangerIntent.ChangeSmoothing(newValue))
        },
        onLaunchGallery = {
            imagePickerLauncher.launch("image/*")
        }
    )
}
