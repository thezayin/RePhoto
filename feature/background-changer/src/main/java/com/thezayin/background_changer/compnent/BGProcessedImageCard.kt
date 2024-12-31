package com.thezayin.background_changer.compnent

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGProcessedImageCard(
    processedImage: Bitmap,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    rotation: Float,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Float, Float) -> Unit,
    onRotationChange: (Float) -> Unit
) {
    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        val newScale = scale * zoomChange
        onScaleChange(newScale)

        val newOffsetX = offsetX + panChange.x
        val newOffsetY = offsetY + panChange.y
        onOffsetChange(newOffsetX, newOffsetY)

        val newRotation = rotation + rotationChange
        onRotationChange(newRotation)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.sdp)
            .clip(RoundedCornerShape(8.sdp))
            .transformable(state = transformState)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY,
                rotationZ = rotation
            )
    ) {
        Image(
            bitmap = processedImage.asImageBitmap(),
            contentDescription = stringResource(id = R.string.content_description_processed_image),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Inside,
            alignment = Alignment.Center
        )
    }
}
