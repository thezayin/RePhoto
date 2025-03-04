package com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes

import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import com.thezayin.editor.utils.drawMode.DrawingConstants

class LineShape(
    private var composePath: Path = Path(),
    private var androidPath: android.graphics.Path = android.graphics.Path(),
    color: Color = Color.White,
    width: Float = DrawingConstants.DEFAULT_STROKE_WIDTH,
    alpha: Float = DrawingConstants.DEFAULT_STROKE_ALPHA
) : AbstractShape(), AndroidShape {

    init {
        updatePaintValues(color = color, width = width, alpha = alpha)
    }

    private var startPoint: Offset = Offset.Zero
    private var endPoint: Offset = Offset.Zero

    override fun draw(drawScope: DrawScope) {
        if (shouldDraw()) {
            drawScope.drawLine(
                start = startPoint,
                end = endPoint,
                color = mColor.copy(alpha = mAlpha),
                strokeWidth = mWidth,
                cap = StrokeCap.Round
            )
        }
    }

    override fun initShape(startX: Float, startY: Float) {
        startPoint = Offset(startX, startY)
        endPoint = startPoint
        composePath = Path().apply { moveTo(startX, startY) }
        androidPath = android.graphics.Path().apply { moveTo(startX, startY) }
    }

    override fun moveShape(endX: Float, endY: Float) {
        endPoint = Offset(endX, endY)
        composePath = Path().apply {
            moveTo(startPoint.x, startPoint.y)
            lineTo(endPoint.x, endPoint.y)
        }
        androidPath.reset()
        androidPath.moveTo(startPoint.x, startPoint.y)
        androidPath.lineTo(endPoint.x, endPoint.y)
    }

    override fun shouldDraw(): Boolean {
        return startPoint != endPoint
    }

    override fun drawOnCanvas(canvas: Canvas) {
        if (shouldDraw()) {
            val paint = android.graphics.Paint().apply {
                color = mColor.copy(alpha = mAlpha).toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = mWidth
                isAntiAlias = true
                strokeCap = android.graphics.Paint.Cap.ROUND
            }
            canvas.drawLine(
                startPoint.x,
                startPoint.y,
                endPoint.x,
                endPoint.y,
                paint
            )
        }
    }
}
