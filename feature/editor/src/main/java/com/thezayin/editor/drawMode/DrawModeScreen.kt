package com.thezayin.editor.drawMode

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import com.thezayin.editor.drawMode.bottomToolbarExtension.DrawModeToolbarExtension
import com.thezayin.editor.drawMode.stateHandling.DrawModeEvent
import com.thezayin.editor.drawMode.stateHandling.DrawModeUiEvent
import com.thezayin.editor.drawMode.toptoolbar.DrawModeTopToolBar
import com.thezayin.editor.editor.presentation.components.BottomToolBarStatic
import com.thezayin.editor.editor.presentation.components.ConfirmationDialog
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_MEDIUM
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_SMALL
import com.thezayin.editor.editor.presentation.event.BottomToolbarEvent
import com.thezayin.editor.editor.presentation.event.toDrawModeEvent
import com.thezayin.editor.utils.ImmutableList
import com.thezayin.editor.utils.drawMode.DrawModeUtils
import com.thezayin.editor.utils.drawMode.getOpacityOrNull
import com.thezayin.editor.utils.drawMode.getShapeTypeOrNull
import com.thezayin.editor.utils.drawMode.getWidthOrNull
import com.thezayin.editor.utils.other.anim.AnimUtils
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun DrawModeScreen(
    onBackPressed: () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: DrawModeViewModel = koinInject()
    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val uiEvent by viewModel.uiEvent.collectAsState(initial = null)
    val backgroundColor = MaterialTheme.colorScheme.background
    val bottomToolbarItems =
        remember { ImmutableList(DrawModeUtils.getDefaultBottomToolbarItemsList()) }
    val topToolbarHeight = TOOLBAR_HEIGHT_SMALL
    val bottomToolbarHeight = TOOLBAR_HEIGHT_MEDIUM
    val aspectRatio = remember(state.currentBitmap) {
        state.currentBitmap?.let { it.width.toFloat() / it.height.toFloat() } ?: 1f
    }
    var triggerCaptureScreenshot by remember { mutableStateOf(false) }
    var triggerNavigateBack by remember { mutableStateOf(false) }
    val scale = state.scale
    val offset = state.offset
    val animatedScale by animateFloatAsState(targetValue = scale)
    val animatedOffset by animateOffsetAsState(targetValue = offset)
    var toolbarVisible by remember { mutableStateOf(false) }
    val showResetZoomPanBtn by remember {
        derivedStateOf { scale != 1f || offset != Offset.Zero }
    }
    LaunchedEffect(key1 = Unit) {
        toolbarVisible = true
        delay(AnimUtils.TOOLBAR_EXPAND_ANIM_DURATION_FAST.toLong())
    }

    val resetZoomAndPan = remember { { viewModel.resetZoomAndPan() } }
    val screenshotState = rememberScreenshotState()
    val onCloseClickedLambda = remember {
        {
            viewModel.resetZoomAndPan()
            if (state.showBottomToolbarExtension) {
                viewModel.onEvent(DrawModeEvent.UpdateToolbarExtensionVisibility(false))
            }
            toolbarVisible = false
            triggerNavigateBack = true
        }
    }

    LaunchedEffect(triggerNavigateBack) {
        if (triggerNavigateBack) {
            delay(200 + AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
            onBackPressed()
            triggerNavigateBack = false
        }
    }

    BackHandler {
        if (state.showBottomToolbarExtension) {
            viewModel.onEvent(DrawModeEvent.UpdateToolbarExtensionVisibility(false))
        } else {
            onCloseClickedLambda()
        }
    }

    val onDoneClickedLambda = remember {
        {
            viewModel.onEvent(DrawModeEvent.Save)
            triggerCaptureScreenshot = true
        }
    }

    LaunchedEffect(triggerCaptureScreenshot) {
        if (triggerCaptureScreenshot) {
            delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION.toLong()) // Adjust delay as needed
            screenshotState.capture()
            triggerCaptureScreenshot = false
        }
    }

    val handleScreenshotResult = remember<(Bitmap) -> Unit> {
        { bitmap ->
            viewModel.onEvent(DrawModeEvent.Share)
        }
    }

    LaunchedEffect(uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is DrawModeUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is DrawModeUiEvent.NavigateBack -> {
                    onBackPressed()
                }

                is DrawModeUiEvent.ShareImage -> {
                    // Handle sharing the image
                    // Implement your share logic here
                    // Example:
                    // shareImage(context, event.bitmap)
                }

                is DrawModeUiEvent.SaveImage -> {
                    // Handle after saving the image
                    // Implement any post-save logic here
                }
            }
        }
    }

    when (val imageState = screenshotState.imageState.value) {
        ImageResult.Initial -> {}
        is ImageResult.Error -> {
            viewModel.onEvent(DrawModeEvent.Close) // Trigger close on error
            // Show a toast or error message
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Failed to capture screenshot.", Toast.LENGTH_SHORT).show()
            }
        }

        is ImageResult.Success -> {
            handleScreenshotResult(imageState.data)
        }
    }

    val onUndoLambda = remember { { viewModel.onEvent(DrawModeEvent.OnUndo) } }
    val onRedoLambda = remember { { viewModel.onEvent(DrawModeEvent.OnRedo) } }
    val onBottomToolbarEventLambda =
        remember<(BottomToolbarEvent) -> Unit> { { viewModel.onEvent(it.toDrawModeEvent()) } }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val (topToolbar, bottomToolbar, bottomToolbarExtension, drawingView, resetZoomPanBtn, _) = createRefs()

        AnimatedVisibility(
            visible = toolbarVisible, modifier = Modifier.constrainAs(topToolbar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }, enter = fadeIn(), exit = fadeOut()
        ) {
            DrawModeTopToolBar(
                modifier = Modifier.fillMaxWidth(),
                undoEnabled = state.canUndo,
                redoEnabled = state.canRedo,
                doneEnabled = state.canUndo || state.canRedo,
                onUndo = onUndoLambda,
                onRedo = onRedoLambda,
                onCloseClicked = onCloseClickedLambda,
                onDoneClicked = onDoneClickedLambda
            )
        }

        ScreenshotBox(
            modifier = Modifier
                .constrainAs(drawingView) {
                    top.linkTo(topToolbar.bottom)
                    bottom.linkTo(bottomToolbar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(top = topToolbarHeight, bottom = bottomToolbarHeight),
            screenshotState = screenshotState
        ) {
            DrawingCanvasContainer(
                state = state,
                scale = animatedScale,
                offset = animatedOffset,
                onDrawingEvent = { event -> viewModel.onEvent(event) },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = toolbarVisible, modifier = Modifier.constrainAs(bottomToolbar) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }, enter = fadeIn(), exit = fadeOut()
        ) {
            BottomToolBarStatic(
                modifier = Modifier.fillMaxWidth(),
                toolbarItems = bottomToolbarItems,
                showColorPickerIcon = true,
                toolbarHeight = bottomToolbarHeight,
                selectedColor = state.selectedColor,
                selectedItem = state.selectedTool,
                onEvent = onBottomToolbarEventLambda
            )
        }

        AnimatedVisibility(
            visible = showResetZoomPanBtn,
            modifier = Modifier
                .constrainAs(resetZoomPanBtn) {
                    top.linkTo(topToolbar.bottom)
                    end.linkTo(parent.end)
                }
                .padding(8.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(8.dp)
                    .size(32.dp)
                    .clickable { resetZoomAndPan() },
                imageVector = ImageVector.vectorResource(id = com.thezayin.values.R.drawable.placeholder_image_3),
                contentDescription = "Reset Zoom and Pan",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            )
        }

        AnimatedVisibility(
            visible = state.showBottomToolbarExtension,
            modifier = Modifier
                .constrainAs(bottomToolbarExtension) {
                    bottom.linkTo(bottomToolbar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    viewModel.onEvent(DrawModeEvent.UpdateToolbarExtensionVisibility(false))
                },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DrawModeToolbarExtension(
                modifier = Modifier.fillMaxWidth(),
                showSeparationAtBottom = true,
                width = state.selectedTool.getWidthOrNull(),
                opacity = state.selectedTool.getOpacityOrNull(),
                shapeType = state.selectedTool.getShapeTypeOrNull(),
                onWidthChange = { newWidth ->
                    viewModel.onEvent(DrawModeEvent.UpdateWidth(newWidth))
                },
                onOpacityChange = { newOpacity ->
                    viewModel.onEvent(DrawModeEvent.UpdateOpacity(newOpacity))
                },
                onShapeTypeChange = { newShapeType ->
                    viewModel.onEvent(DrawModeEvent.UpdateShapeType(newShapeType))
                }
            )
        }

        if (state.showExitConfirmation) {
            ConfirmationDialog(
                title = "Exit Drawing",
                text = "Are you sure you want to exit without saving?",
                onConfirm = {
                    viewModel.onEvent(DrawModeEvent.ConfirmExit)
                },
                onDismiss = {
                    viewModel.onEvent(DrawModeEvent.DismissExit)
                }
            )
        }
    }
}