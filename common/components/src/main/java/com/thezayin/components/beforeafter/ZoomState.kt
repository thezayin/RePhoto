package com.thezayin.components.beforeafter

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope

@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): ZoomState {
    return remember(key1) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomEnabled = zoomEnabled,
            panEnabled = panEnabled,
            rotationEnabled = rotationEnabled,
            limitPan = limitPan
        )
    }
}

@Immutable
open class ZoomState internal constructor(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    private val zoomEnabled: Boolean = true,
    private val panEnabled: Boolean = true,
    internal val rotationEnabled: Boolean = true,
    internal val limitPan: Boolean = false
) {

    private val zoomMin = minZoom.coerceAtLeast(.5f)
    private val zoomMax = maxZoom.coerceAtLeast(1f)
    private val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    private val rotationInitial = initialRotation % 360

    private val animatablePan = Animatable(Offset.Zero, Offset.VectorConverter)
    private val animatableZoom = Animatable(zoomInitial)
    private val animatableRotation = Animatable(rotationInitial)

    init {
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = animatablePan.value

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    internal open suspend fun updateZoomState(
        size: IntSize,
        gesturePan: Offset,
        gestureZoom: Float,
        gestureRotate: Float = 1f,
    ) {
        val zoom = (zoom * gestureZoom).coerceIn(zoomMin, zoomMax)
        val rotation = if (rotationEnabled) {
            rotation + gestureRotate
        } else {
            0f
        }

        if (panEnabled) {
            val offset = pan
            var newOffset = offset + gesturePan.times(zoom)
            val boundPan = limitPan && !rotationEnabled

            if (boundPan) {
                val maxX = (size.width * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)
                val maxY = (size.height * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)
                newOffset = Offset(
                    newOffset.x.coerceIn(-maxX, maxX),
                    newOffset.y.coerceIn(-maxY, maxY)
                )
            }
            snapPanTo(newOffset)
        }

        if (zoomEnabled) {
            snapZoomTo(zoom)
        }

        if (rotationEnabled) {
            snapRotationTo(rotation)
        }
    }

    internal suspend fun animatePanTo(pan: Offset) = coroutineScope {
        animatablePan.animateTo(pan)
    }

    internal suspend fun animateZoomTo(zoom: Float) = coroutineScope {
        animatableZoom.animateTo(zoom)
    }

    private suspend fun snapPanTo(offset: Offset) = coroutineScope {
        if (panEnabled) {
            animatablePan.snapTo(offset)
        }
    }

    private suspend fun snapZoomTo(zoom: Float) = coroutineScope {
        if (zoomEnabled) {
            animatableZoom.snapTo(zoom)
        }
    }

    private suspend fun snapRotationTo(rotation: Float) = coroutineScope {
        if (rotationEnabled) {
            animatableRotation.snapTo(rotation)
        }
    }
}
