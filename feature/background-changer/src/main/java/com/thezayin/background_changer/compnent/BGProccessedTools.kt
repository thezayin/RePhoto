package com.thezayin.background_changer.compnent

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGProcessedTools(
    onMore: () -> Unit,
    backgroundImage: Bitmap?,
    processedImage: Bitmap?,
    baseImage: Bitmap?,
    isShowingBaseImage: Boolean,
    setDrawableBackground: (Bitmap) -> Unit,
    onComparePressStart: () -> Unit,
    onComparePressEnd: () -> Unit,
    imageScale: Float,
    imageOffsetX: Float,
    imageOffsetY: Float,
    imageRotation: Float,
    onImageScaleChange: (Float) -> Unit,
    onImageOffsetChange: (Float, Float) -> Unit,
    onImageRotationChange: (Float) -> Unit,
    isLoading: Boolean = false,
    onToggleSettings: () -> Unit,
    isSettingsOpen: Boolean,
    onGalleryLaunch: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (!isShowingBaseImage) {
                backgroundImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.content_description_background_image),
                        modifier = Modifier.fillMaxSize(),
                        alignment = Alignment.Center
                    )
                }

                processedImage?.let {
                    BGProcessedImageCard(
                        processedImage = it,
                        scale = imageScale,
                        offsetX = imageOffsetX,
                        offsetY = imageOffsetY,
                        rotation = imageRotation,
                        onScaleChange = onImageScaleChange,
                        onOffsetChange = onImageOffsetChange,
                        onRotationChange = onImageRotationChange
                    )
                }
            } else {
                baseImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.content_description_base_image),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.sdp)
                            .clip(RoundedCornerShape(8.sdp)),
                        alignment = Alignment.Center
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.sdp),
            horizontalArrangement = Arrangement.spacedBy(12.sdp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BGSmoothingIcon(
                isSettingsOpen = isSettingsOpen,
                onToggleSettings = onToggleSettings
            )
            BGCompareIcon(
                onPressStart = onComparePressStart,
                onPressEnd = onComparePressEnd
            )
        }

        BGOptions(
            onSelectGallery = onGalleryLaunch,
            onSelectBackground = { bitmap ->
                setDrawableBackground(bitmap)
            },
            onMore = onMore,
            isLoading = isLoading
        )
    }
}