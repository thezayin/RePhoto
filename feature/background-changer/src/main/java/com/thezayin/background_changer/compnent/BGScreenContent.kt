package com.thezayin.background_changer.compnent

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.thezayin.background_changer.component.BGSmoothingBottomSheet
import com.thezayin.background_changer.state.ChangerViewState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BGScreenContent(
    state: ChangerViewState,
    onSelectedCategory: (String) -> Unit,
    setDrawableBackground: (Bitmap) -> Unit,
    onCompareLongPressStart: () -> Unit,
    onCompareLongPressEnd: () -> Unit,
    onImageScaleChange: (Float) -> Unit,
    onImageOffsetChange: (Float, Float) -> Unit,
    onImageRotationChange: (Float) -> Unit,
    applySmoothing: (Float) -> Unit,
    onLaunchGallery: () -> Unit,
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var isSettingsOpen by remember { mutableStateOf(false) }

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            // Optional: Add a TopBar if needed
        },
        containerColor = colorResource(R.color.black)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                BGLoadingOverlay(
                    modifier = Modifier.align(Alignment.Center),
                    statusText = state.statusText
                )
            }

            if (state.isError) {
                Text(
                    text = stringResource(
                        id = R.string.error_message_format,
                        state.errorMessage ?: stringResource(id = R.string.error_generic)
                    ),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red,
                    fontSize = 16.ssp
                )
            }

            if (state.processedImage != null || state.baseImage != null) {
                BGProcessedTools(
                    backgroundImage = state.backgroundImage,
                    processedImage = state.processedImage,
                    baseImage = state.baseImage,
                    isShowingBaseImage = state.isShowingBaseImage,
                    onMore = {
                        isBottomSheetVisible = true
                    },
                    setDrawableBackground = setDrawableBackground,
                    onComparePressStart = {
                        onCompareLongPressStart()
                    },
                    onComparePressEnd = {
                        onCompareLongPressEnd()
                    },
                    imageScale = state.imageScale,
                    imageOffsetX = state.imageOffsetX,
                    imageOffsetY = state.imageOffsetY,
                    imageRotation = state.imageRotation,
                    onImageScaleChange = onImageScaleChange,
                    onImageOffsetChange = onImageOffsetChange,
                    onImageRotationChange = onImageRotationChange,
                    isLoading = state.isLoading,
                    onToggleSettings = {
                        isSettingsOpen = !isSettingsOpen
                    },
                    isSettingsOpen = isSettingsOpen,
                    onGalleryLaunch = onLaunchGallery
                )

                if (isBottomSheetVisible) {
                    ModalBottomSheet(
                        onDismissRequest = { isBottomSheetVisible = false },
                        containerColor = colorResource(R.color.gray_level_3)
                    ) {
                        BGOptionsSheet(
                            categories = state.backgroundImages.map { it.name },
                            imagesByCategory = state.backgroundImages.associate { it.name to it.imageUrls },
                            onImageSelected = { imageRes ->
                                onSelectedCategory(imageRes)
                                isBottomSheetVisible = false
                            },
                            onCategorySelected = { category ->
                                selectedCategory = category
                            },
                            isLoading = state.isLoading
                        )
                    }
                }

                if (isSettingsOpen) {
                    ModalBottomSheet(
                        onDismissRequest = { isSettingsOpen = false },
                        sheetState = settingsSheetState,
                        containerColor = colorResource(R.color.gray_level_3)
                    ) {
                        BGSmoothingBottomSheet(
                            currentSmoothing = state.smoothingValue,
                            onSmoothingChange = applySmoothing

                        )
                    }
                }

                if (state.isLoading) {
                    BGLoadingOverlay(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        statusText = state.statusText
                    )
                }
            }
        }
    }
}