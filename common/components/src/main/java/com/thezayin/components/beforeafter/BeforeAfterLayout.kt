package com.thezayin.components.beforeafter

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.launch

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    triggerAnimationKey: Int,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {
    val progress = remember { Animatable(50f) }
    var isAnimationCompleted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(triggerAnimationKey) {
        try {
            val animationSpec =
                androidx.compose.animation.core.tween<Float>(durationMillis = 600)
            progress.animateTo(targetValue = 0f, animationSpec = animationSpec)
            progress.animateTo(targetValue = 50f, animationSpec = animationSpec)
            progress.animateTo(targetValue = 100f, animationSpec = animationSpec)
            progress.animateTo(targetValue = 50f, animationSpec = animationSpec)
            isAnimationCompleted = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Layout(modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress.value,
        onProgressChange = { newProgress ->
            if (isAnimationCompleted) {
                coroutineScope.launch {
                    progress.snapTo(newProgress)
                }
            }
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                overlayStyle = overlayStyle
            )
        })
}