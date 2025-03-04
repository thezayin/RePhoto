package com.thezayin.editor.drawMode.bottomToolbarExtension

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.utils.drawMode.DrawingConstants

@Composable
fun DrawModeToolbarExtension(
    modifier: Modifier,
    showSeparationAtBottom: Boolean = true,
    width: Float? = null,
    onWidthChange: (Float) -> Unit = {},
    opacity: Float? = null,
    onOpacityChange: (Float) -> Unit = {},
    shapeType: ShapeType? = null,
    onShapeTypeChange: (ShapeType) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {

        // Width Slider
        width?.let {
            CustomSliderItem(
                modifier = Modifier.padding(8.dp),
                sliderLabel = "Width",
                sliderValue = it,
                minValue = DrawingConstants.MINIMUM_STROKE_WIDTH,
                maxValue = DrawingConstants.MAXIMUM_STROKE_WIDTH,
                onValueChange = onWidthChange
            )
        }

        // Opacity Slider
        opacity?.let {
            CustomSliderItem(
                modifier = Modifier.padding(8.dp),
                sliderLabel = "Opacity",
                sliderValue = it,
                minValue = 0f,
                maxValue = 100f,
                onValueChange = onOpacityChange
            )
        }

        // Shape Type Radio Buttons
        shapeType?.let {
            ShapeTypeRadioButtons(
                selectedShape = it, onShapeSelected = onShapeTypeChange
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Optional separation line
        if (showSeparationAtBottom) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun ShapeTypeRadioButtons(
    selectedShape: ShapeType, onShapeSelected: (ShapeType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ShapeType.values().forEach { shape ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onShapeSelected(shape) }) {
                androidx.compose.material3.RadioButton(selected = selectedShape == shape,
                    onClick = { onShapeSelected(shape) })
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = shape.name)
            }
        }
    }
}
