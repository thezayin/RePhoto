package com.thezayin.components.beforeafter

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


@Composable
internal fun DefaultOverlay(
    width: Dp,
    height: Dp,
    position: Offset,
    overlayStyle: OverlayStyle,
    thumbPosition: Float? = null,
    @DrawableRes thumbRes: Int? = null,
    thumbTint: Color? = null,
    showMessage: Boolean = false,
    thumbSize: Dp = 37.dp
) {
    val verticalThumbMove = overlayStyle.verticalThumbMove
    val dividerColor = overlayStyle.dividerColor
    val dividerWidth = overlayStyle.dividerWidth
    val thumbBackgroundColor = overlayStyle.thumbBackgroundColor
    val thumbTintColor = thumbTint ?: overlayStyle.thumbTintColor
    val thumbShape = overlayStyle.thumbShape
    val thumbElevation = overlayStyle.thumbElevation
    val thumbResource = thumbRes ?: overlayStyle.thumbResource
    val thumbPositionPercent = thumbPosition ?: overlayStyle.thumbPositionPercent

    var thumbPosX = position.x
    var thumbPosY = position.y

    val linePosition: Float

    val density = LocalDensity.current

    with(density) {
        val thumbRadius = (thumbSize / 2).toPx()
        val imageWidthInPx = width.toPx()
        val imageHeightInPx = height.toPx()

        val horizontalOffset = imageWidthInPx / 2
        val verticalOffset = imageHeightInPx / 2

        linePosition = thumbPosX.coerceIn(0f, imageWidthInPx)
        thumbPosX -= horizontalOffset

        thumbPosY = if (verticalThumbMove) {
            (thumbPosY - verticalOffset)
                .coerceIn(
                    -verticalOffset + thumbRadius,
                    verticalOffset - thumbRadius
                )
        } else {
            ((imageHeightInPx * thumbPositionPercent / 100f - thumbRadius) - verticalOffset)
        }
    }

    Box(
        modifier = Modifier.size(width, height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            drawLine(
                dividerColor,
                strokeWidth = dividerWidth.toPx(),
                start = Offset(linePosition, 0f),
                end = Offset(linePosition, size.height)
            )
        }
        if (showMessage && thumbPosX == 0f) {
            val yOffset = thumbPosY.toInt().minus(135)
            Box(
                modifier = Modifier
                    .height(54.dp)
                    .offset { IntOffset(thumbPosX.toInt(), yOffset) }
                    .paint(
                        painterResource(id = com.thezayin.values.R.drawable.ic_advertise)
                    )
            ) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.TopCenter),
                    text = "Move",
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
        Icon(
            painter = painterResource(id = thumbResource),
            contentDescription = null,
            tint = thumbTintColor,
            modifier = Modifier
                .offset {
                    IntOffset(thumbPosX.toInt(), thumbPosY.toInt())
                }
                .shadow(thumbElevation, thumbShape)
                .background(thumbBackgroundColor)
                .size(thumbSize)
                .padding(3.dp)
        )
    }
}

@Immutable
class OverlayStyle(
    val dividerColor: Color = Color.White,
    val dividerWidth: Dp = 1.5.dp,
    val verticalThumbMove: Boolean = false,
    val thumbBackgroundColor: Color = Color.White,
    val thumbTintColor: Color = Color.Black,
    val thumbShape: Shape = CircleShape,
    val thumbElevation: Dp = 2.dp,
    @DrawableRes val thumbResource: Int = com.thezayin.values.R.drawable.thumb_res,
    val thumbSize: Dp = 36.dp,
    @FloatRange(from = 0.0, to = 100.0) val thumbPositionPercent: Float = 50f,
)
