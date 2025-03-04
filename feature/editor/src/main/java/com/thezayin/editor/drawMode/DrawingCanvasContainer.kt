package com.thezayin.editor.drawMode

import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.AbstractShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.BrushShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.LineShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.OvalShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.PenShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.RectangleShape
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.ShapeType
import com.thezayin.editor.drawMode.drawingCanvas.models.PathDetails
import com.thezayin.editor.drawMode.stateHandling.DrawModeEvent
import com.thezayin.editor.drawMode.stateHandling.DrawModeState
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.utils.drawMode.CustomLayerTypeComposable
import com.thezayin.editor.utils.drawMode.DrawingConstants
import com.thezayin.editor.drawMode.drawingCanvas.drawingTool.shapes.AndroidShape // Ensure this import exists

@Composable
fun DrawingCanvasContainer(
    state: DrawModeState,
    scale: Float,
    offset: Offset,
    onDrawingEvent: (DrawModeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = state.currentBitmap
    val coroutineScope = rememberCoroutineScope()

    if (bitmap != null) {
        val aspectRatio = remember(bitmap) {
            bitmap.width.toFloat() / bitmap.height.toFloat()
        }

        // Temporary shape being drawn
        var currentShape by remember { mutableStateOf<AbstractShape?>(null) }

        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            // Initialize a new shape based on the selected tool
                            val newShape: AbstractShape = when (val tool = state.selectedTool) {
                                is BottomToolbarItemState.BrushTool -> PenShape(
                                    composePath = Path().apply {
                                        moveTo(
                                            startOffset.x,
                                            startOffset.y
                                        )
                                    },
                                    androidPath = android.graphics.Path()
                                        .apply { moveTo(startOffset.x, startOffset.y) },
                                    color = state.selectedColor,
                                    width = tool.width.takeIf { it > 0 }
                                        ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                    alpha = tool.opacity.takeIf { it in 0f..1f }
                                        ?: DrawingConstants.DEFAULT_STROKE_ALPHA
                                )

                                is BottomToolbarItemState.EraserTool -> BrushShape(
                                    composePath = Path().apply {
                                        moveTo(
                                            startOffset.x,
                                            startOffset.y
                                        )
                                    },
                                    androidPath = android.graphics.Path()
                                        .apply { moveTo(startOffset.x, startOffset.y) },
                                    isEraser = true,
                                    color = Color.Transparent,
                                    width = tool.width.takeIf { it > 0 }
                                        ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                    alpha = DrawingConstants.DEFAULT_STROKE_ALPHA
                                )

                                is BottomToolbarItemState.ShapeTool -> {
                                    when (tool.shapeType) {
                                        ShapeType.LINE -> LineShape(
                                            composePath = Path().apply {
                                                moveTo(
                                                    startOffset.x,
                                                    startOffset.y
                                                )
                                            },
                                            androidPath = android.graphics.Path()
                                                .apply { moveTo(startOffset.x, startOffset.y) },
                                            color = state.selectedColor,
                                            width = tool.width.takeIf { it > 0 }
                                                ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                            alpha = tool.opacity.takeIf { it in 0f..1f }
                                                ?: DrawingConstants.DEFAULT_STROKE_ALPHA
                                        )

                                        ShapeType.RECTANGLE -> RectangleShape(
                                            composePath = Path().apply {
                                                moveTo(
                                                    startOffset.x,
                                                    startOffset.y
                                                )
                                            },
                                            androidPath = android.graphics.Path()
                                                .apply { moveTo(startOffset.x, startOffset.y) },
                                            color = state.selectedColor,
                                            width = tool.width.takeIf { it > 0 }
                                                ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                            alpha = tool.opacity.takeIf { it in 0f..1f }
                                                ?: DrawingConstants.DEFAULT_STROKE_ALPHA
                                        )

                                        ShapeType.OVAL -> OvalShape(
                                            composePath = Path().apply {
                                                moveTo(
                                                    startOffset.x,
                                                    startOffset.y
                                                )
                                            },
                                            androidPath = android.graphics.Path()
                                                .apply { moveTo(startOffset.x, startOffset.y) },
                                            color = state.selectedColor,
                                            width = tool.width.takeIf { it > 0 }
                                                ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                            alpha = tool.opacity.takeIf { it in 0f..1f }
                                                ?: DrawingConstants.DEFAULT_STROKE_ALPHA
                                        )
                                        // Handle additional ShapeTypes if any
                                        else -> PenShape(
                                            composePath = Path().apply {
                                                moveTo(
                                                    startOffset.x,
                                                    startOffset.y
                                                )
                                            },
                                            androidPath = android.graphics.Path()
                                                .apply { moveTo(startOffset.x, startOffset.y) },
                                            color = state.selectedColor,
                                            width = tool.width.takeIf { it > 0 }
                                                ?: DrawingConstants.DEFAULT_STROKE_WIDTH,
                                            alpha = tool.opacity.takeIf { it in 0f..1f }
                                                ?: DrawingConstants.DEFAULT_STROKE_ALPHA
                                        )
                                    }
                                }

                                else -> PenShape(
                                    composePath = Path().apply {
                                        moveTo(
                                            startOffset.x,
                                            startOffset.y
                                        )
                                    },
                                    androidPath = android.graphics.Path()
                                        .apply { moveTo(startOffset.x, startOffset.y) },
                                    color = state.selectedColor,
                                    width = DrawingConstants.DEFAULT_STROKE_WIDTH,
                                    alpha = DrawingConstants.DEFAULT_STROKE_ALPHA
                                )
                            }

                            // Initialize the shape
                            newShape.initShape(startOffset.x, startOffset.y)
                            currentShape = newShape
                        },
                        onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            currentShape?.let { shape ->
                                shape.moveShape(change.position.x, change.position.y)
                            }
                        },
                        onDragEnd = {
                            currentShape?.let { shape ->
                                if (shape.shouldDraw()) {
                                    // Emit the AddNewPath event with the completed shape
                                    onDrawingEvent(DrawModeEvent.AddNewPath(PathDetails(drawingShape = shape)))
                                }
                            }
                            currentShape = null
                        },
                        onDragCancel = {
                            currentShape = null
                        }
                    )
                }
        ) {
            // Display the base bitmap
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Base Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Overlay for drawing paths
            CustomLayerTypeComposable(
                layerType = View.LAYER_TYPE_HARDWARE,
                modifier = Modifier.fillMaxSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(aspectRatio)
                        .align(Alignment.Center)
                ) {
                    // Draw all existing shapes
                    state.pathDetailList.forEach { pathDetails ->
                        // Safely cast to AndroidShape before calling drawOnCanvas
                        (pathDetails.drawingShape as? AndroidShape)?.drawOnCanvas(drawContext.canvas.nativeCanvas)
                    }

                    // Draw the current shape being drawn
                    currentShape?.let { shape ->
                        // Safely cast to AndroidShape before calling drawOnCanvas
                        (shape as? AndroidShape)?.drawOnCanvas(drawContext.canvas.nativeCanvas)
                    }
                }
            }
        }
    }
}
