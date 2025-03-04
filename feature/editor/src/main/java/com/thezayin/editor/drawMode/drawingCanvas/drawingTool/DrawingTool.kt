package com.thezayin.editor.drawMode.drawingCanvas.drawingTool

import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType

/**
 * TODO: delete this class (this is not used in EditorScreen implementation)
 */
sealed class DrawingTool {
    object Brush : DrawingTool()

    object Eraser : DrawingTool()

    class Shape(
        val shapeType: ShapeType
    ) : DrawingTool()
}