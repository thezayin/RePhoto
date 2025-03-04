package com.thezayin.editor.utils.drawMode

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.AbstractShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.BrushShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.LineShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.OvalShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.RectangleShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import kotlin.math.cos
import kotlin.math.sin

object DrawModeUtils {

    const val DEFAULT_SELECTED_INDEX = 2

    fun getDefaultBottomToolbarItemsList(): ArrayList<BottomToolbarItemState> {
        return arrayListOf(
            BottomToolbarItemState.ColorItemState,
            BottomToolbarItemState.PanItemState,
            BottomToolbarItemState.BrushTool(
                width = DrawingConstants.DEFAULT_STROKE_WIDTH,
                opacity = DrawingConstants.DEFAULT_STROKE_OPACITY
            ),
            BottomToolbarItemState.ShapeTool(
                width = DrawingConstants.DEFAULT_STROKE_WIDTH,
                opacity = DrawingConstants.DEFAULT_STROKE_OPACITY,
                shapeType = ShapeType.LINE
            ),
            BottomToolbarItemState.EraserTool(
                width = DrawingConstants.DEFAULT_STROKE_WIDTH
            ),
        )
    }

    /**
     * This function uses trigonometric functions to compute the new offset values after rotation.
     * new_x = x * cos(θ) - y * sin(θ)
     * new_y = x * sin(θ) + y * cos(θ)
     */
    fun rotateOffset(
        offset: Offset,
        angleDegrees: Float
    ): Offset {
        val angleRadians = Math.toRadians(angleDegrees.toDouble())
        val cosAngle = cos(angleRadians)
        val sinAngle = sin(angleRadians)
        val x = offset.x * cosAngle - offset.y * sinAngle
        val y = offset.x * sinAngle + offset.y * cosAngle
        return Offset(x.toFloat(), y.toFloat())
    }
}

fun BottomToolbarItemState.getShape(
    selectedColor: Color,
    scale: Float = 1f,
): AbstractShape {
    return when (val toolbarItem = this) {
        is BottomToolbarItemState.BrushTool -> {
            BrushShape(
                color = selectedColor,
                width = toolbarItem.width / scale,
                alpha = toolbarItem.opacity / 100f
            )
        }

        is BottomToolbarItemState.ShapeTool -> when (toolbarItem.shapeType) {
            ShapeType.LINE -> LineShape(
                color = selectedColor,
                width = toolbarItem.width / scale,
                alpha = toolbarItem.opacity / 100f
            )

            ShapeType.OVAL -> OvalShape(
                color = selectedColor,
                width = toolbarItem.width / scale,
                alpha = toolbarItem.opacity / 100f
            )

            ShapeType.RECTANGLE -> RectangleShape(
                color = selectedColor,
                width = toolbarItem.width / scale,
                alpha = toolbarItem.opacity / 100f
            )
        }

        else -> {
            /**
             * this else block represents the EraserTool, as any other item (ColorItem, PanItem) won't be sent here
             */
            val width =
                if (toolbarItem is BottomToolbarItemState.EraserTool) toolbarItem.width
                else DrawingConstants.DEFAULT_STROKE_WIDTH
            BrushShape(
                isEraser = true,
                width = width / scale
            )
        }
    }
}

fun BottomToolbarItemState.getWidthOrNull(): Float? {
    return when (this) {
        is BottomToolbarItemState.BrushTool -> this.width
        is BottomToolbarItemState.EraserTool -> this.width
        is BottomToolbarItemState.ShapeTool -> this.width
        else -> null
    }
}

fun BottomToolbarItemState.setWidthIfPossible(mWidth: Float): BottomToolbarItemState {
    when (this) {
        is BottomToolbarItemState.BrushTool -> this.width = mWidth
        is BottomToolbarItemState.EraserTool -> this.width = mWidth
        is BottomToolbarItemState.ShapeTool -> this.width = mWidth
        else -> {}
    }
    return this
}

fun BottomToolbarItemState.getOpacityOrNull(): Float? {
    return when (this) {
        is BottomToolbarItemState.BrushTool -> this.opacity
        is BottomToolbarItemState.ShapeTool -> this.opacity
        else -> null
    }
}

fun BottomToolbarItemState.setOpacityIfPossible(mOpacity: Float): BottomToolbarItemState {
    when (this) {
        is BottomToolbarItemState.BrushTool -> this.opacity = mOpacity
        is BottomToolbarItemState.ShapeTool -> this.opacity = mOpacity
        else -> {}
    }
    return this
}

fun BottomToolbarItemState.getShapeTypeOrNull(): ShapeType? {
    return when (this) {
        is BottomToolbarItemState.ShapeTool -> this.shapeType
        else -> null
    }
}

fun BottomToolbarItemState.setShapeTypeIfPossible(mShapeType: ShapeType): BottomToolbarItemState {
    when (this) {
        is BottomToolbarItemState.ShapeTool -> this.shapeType = mShapeType
        else -> {}
    }
    return this
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}

@Composable
fun Float.pxToDp(): Dp {
    return with(LocalDensity.current) {
        this@pxToDp.toDp()
    }
}