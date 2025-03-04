package com.thezayin.editor.editor.presentation.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.caseOptions.TextCaseType
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.styleOptions.TextStyleAttr

@Immutable
sealed class BottomToolbarItemState {
    // ---------- DrawModeScreen Items - START
    object ColorItemState : BottomToolbarItemState()
    object PanItemState : BottomToolbarItemState()

    data class BrushTool(
        var width: Float = 5f,
        var opacity: Float = 1f
    ) : BottomToolbarItemState()

    data class ShapeTool(
        var width: Float = 5f,
        var opacity: Float = 1f,
        var shapeType: ShapeType = ShapeType.RECTANGLE
    ) : BottomToolbarItemState()

    data class EraserTool(
        var width: Float = 10f
    ) : BottomToolbarItemState()
    // ---------- DrawModeScreen Items - End


    // ---------- TextModeScreen Items - START
    object AddItemState : BottomToolbarItemState()
    class TextFormat(
        var textStyleAttr: TextStyleAttr,
        var textCaseType: TextCaseType,
        var textAlign: TextAlign,
    ) : BottomToolbarItemState()
    class TextFontFamily(var textFontFamily: FontFamily) : BottomToolbarItemState()
    // ---------- TextModeScreen Items - End


    object NONE : BottomToolbarItemState()
}