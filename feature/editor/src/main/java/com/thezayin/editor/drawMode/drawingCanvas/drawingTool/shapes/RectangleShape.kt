package com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes

import android.graphics.Canvas
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import com.thezayin.editor.utils.drawMode.DrawingConstants

class RectangleShape(
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
            val rect = Rect(
                left = startPoint.x.coerceAtMost(endPoint.x),
                top = startPoint.y.coerceAtMost(endPoint.y),
                right = startPoint.x.coerceAtLeast(endPoint.x),
                bottom = startPoint.y.coerceAtLeast(endPoint.y)
            )
            drawScope.drawRect(
                topLeft = rect.topLeft,
                size = rect.size,
                color = mColor.copy(alpha = mAlpha),
                style = Stroke(
                    width = mWidth,
                    cap = StrokeCap.Round
                )
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
        val rect = Rect(
            left = startPoint.x.coerceAtMost(endPoint.x),
            top = startPoint.y.coerceAtMost(endPoint.y),
            right = startPoint.x.coerceAtLeast(endPoint.x),
            bottom = startPoint.y.coerceAtLeast(endPoint.y)
        )
        composePath = Path().apply { addRect(rect) }
        androidPath.reset()
        androidPath.addRect(
            android.graphics.RectF(rect.left, rect.top, rect.right, rect.bottom),
            android.graphics.Path.Direction.CW
        )
    }

    override fun shouldDraw(): Boolean {
        return startPoint != endPoint
    }

    override fun drawOnCanvas(canvas: Canvas) {
        if (shouldDraw()) {
            // Compute the bounds of the androidPath
            val rectF = RectF()
            androidPath.computeBounds(rectF, true)

            val paint = android.graphics.Paint().apply {
                color = mColor.copy(alpha = mAlpha).toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = mWidth
                isAntiAlias = true
            }
            // Use RectF to avoid ambiguity
            canvas.drawRect(rectF, paint)
        }
    }
}
