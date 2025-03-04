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

class PenShape(
    private var composePath: Path = Path(),
    private var androidPath: android.graphics.Path = android.graphics.Path(),
    color: Color = Color.White,
    width: Float = DrawingConstants.DEFAULT_STROKE_WIDTH,
    alpha: Float = DrawingConstants.DEFAULT_STROKE_ALPHA
) : AbstractShape(), AndroidShape {

    init {
        updatePaintValues(color = color, width = width, alpha = alpha)
    }

    override fun draw(drawScope: DrawScope) {
        drawScope.drawPath(
            path = composePath,
            color = mColor.copy(alpha = mAlpha),
            style = Stroke(
                width = mWidth,
                cap = StrokeCap.Round,
                pathEffect = null
            )
        )
    }

    override fun initShape(startX: Float, startY: Float) {
        composePath = Path().apply { moveTo(startX, startY) }
        androidPath = android.graphics.Path().apply { moveTo(startX, startY) }
    }

    override fun moveShape(endX: Float, endY: Float) {
        composePath.lineTo(endX, endY)
        androidPath.lineTo(endX, endY)
    }

    override fun shouldDraw(): Boolean {
        // Determine if the path has significant length to be drawn
        return !composePath.isEmpty
    }

    override fun drawOnCanvas(canvas: Canvas) {
        val paint = android.graphics.Paint().apply {
            color = mColor.copy(alpha = mAlpha).toArgb()
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = mWidth
            isAntiAlias = true
            strokeCap = android.graphics.Paint.Cap.ROUND
        }
        canvas.drawPath(androidPath, paint)
    }
}
