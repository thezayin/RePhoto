package com.thezayin.editor.textMode

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.thezayin.editor.textMode.topToolbar.TextModeTopToolbar
import com.abizer_r.quickedit.ui.textMode.TextModeViewModel
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import com.thezayin.editor.common.AnimatedToolbarContainer
import com.thezayin.editor.common.bottomToolbarModifier
import com.thezayin.editor.common.topToolbarModifier
import com.thezayin.editor.editor.presentation.components.BottomToolBarStatic
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_MEDIUM
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_SMALL
import com.thezayin.editor.editor.presentation.event.BottomToolbarEvent
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.textMode.TextModeEvent.AddTransformableTextBox
import com.thezayin.editor.textMode.TextModeEvent.HideTextEditor
import com.thezayin.editor.textMode.TextModeEvent.ShowTextEditor
import com.thezayin.editor.textMode.TextModeEvent.UpdateToolbarExtensionVisibility
import com.thezayin.editor.textMode.bottomToolbarExtension.TextModeToolbarExtension
import com.thezayin.editor.textMode.textEditorLayout.TextEditorLayout
import com.thezayin.editor.textMode.textEditorLayout.TextEditorState
import com.thezayin.editor.transformableViews.base.TransformableTextBoxState
import com.thezayin.editor.utils.defaultErrorToast
import com.thezayin.editor.utils.other.anim.AnimUtils
import com.thezayin.editor.utils.other.bitmap.ImmutableBitmap
import com.thezayin.editor.utils.textMode.TextModeUtils.BorderForSelectedViews
import com.thezayin.editor.utils.textMode.TextModeUtils.DrawAllTransformableViews
import com.thezayin.editor.utils.textMode.blurBackground.BlurBitmapBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TextModeScreen(
    modifier: Modifier = Modifier,
    immutableBitmap: ImmutableBitmap,
    onDoneClicked: (bitmap: Bitmap) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val viewModel: TextModeViewModel = koinInject()
    val state by viewModel.state.collectAsStateWithLifecycle(
        lifecycleOwner = lifeCycleOwner
    )
    val showTextEditor by viewModel.showTextEditor.collectAsStateWithLifecycle(
        lifecycleOwner = lifeCycleOwner
    )
    // TODO - text: animate the toolbar changes (hide/show other options)
    val bottomToolbarItems by viewModel.bottomToolbarItemsState.collectAsStateWithLifecycle(
        lifecycleOwner = lifeCycleOwner
    )
    val selectedTool: BottomToolbarItemState =
        remember(state.showBottomToolbarExtension, state.selectedTool) {
            if (state.showBottomToolbarExtension)
                state.selectedTool
            else
                BottomToolbarItemState.NONE
        }
    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(key1 = Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }

    val topToolbarHeight = TOOLBAR_HEIGHT_SMALL
    val bottomToolbarHeight = TOOLBAR_HEIGHT_MEDIUM

    var toolbarVisible by remember { mutableStateOf(false) }

    val screenshotState = rememberScreenshotState()

    LaunchedEffect(key1 = Unit) {
        toolbarVisible = true
        delay(AnimUtils.TOOLBAR_EXPAND_ANIM_DURATION_FAST.toLong())
        viewModel.onEvent(ShowTextEditor())
    }

    val onCloseClickedLambda = remember<() -> Unit> {
        {
            lifeCycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (state.showBottomToolbarExtension) {
                    viewModel.onEvent(UpdateToolbarExtensionVisibility(false))
                    delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION.toLong())
                }
                toolbarVisible = false
                delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
                onBackPressed()
            }
        }
    }

    BackHandler {
        if (showTextEditor) {
            viewModel.onEvent(HideTextEditor)
        } else if (state.showBottomToolbarExtension) {
            viewModel.onEvent(UpdateToolbarExtensionVisibility(false))
        } else {
            onCloseClickedLambda()
        }
    }

    val onDoneClickedLambda = remember<() -> Unit> {
        {
            viewModel.handleStateBeforeCaptureScreenshot()
            lifeCycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                delay(400)  /* Delay to update the ToolbarExtensionView Visibility and view-selection in ui */
                screenshotState.capture()
            }
        }
    }

    val onCloseClickedTextEditorLambda = remember<() -> Unit> {
        {
            viewModel.onEvent(HideTextEditor)
        }
    }

    val onDoneClickedTextEditorLambda = remember<(TextEditorState) -> Unit> {
        { editorState ->
            viewModel.onEvent(
                AddTransformableTextBox(
                    textBoxState = TransformableTextBoxState(
                        id = editorState.textStateId,
                        text = editorState.text,
                        textColor = editorState.selectedColor,
                        textAlign = editorState.textAlign,
                        textFont = editorState.textFont
                    )
                )
            )
        }
    }

    val handleScreenshotResult = remember<(Bitmap) -> Unit> {
        { bitmap ->
            lifeCycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                toolbarVisible = false
                delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
                onDoneClicked(bitmap)
            }
        }
    }

    when (screenshotState.imageState.value) {
        ImageResult.Initial -> {}
        is ImageResult.Error -> {
            viewModel.shouldGoToNextScreen = false
            context.defaultErrorToast()
        }

        is ImageResult.Success -> {
            if (viewModel.shouldGoToNextScreen) {
                viewModel.shouldGoToNextScreen = false
                screenshotState.bitmap?.let { mBitmap ->
                    handleScreenshotResult(mBitmap)
                } ?: context.defaultErrorToast()
            }
        }
    }

    val onBgClickedLambda = remember<() -> Unit> {
        {
            viewModel.updateViewSelection(null)
        }
    }

    val onBottomToolbarEventLambda = remember<(BottomToolbarEvent) -> Unit> {
        {
            viewModel.onBottomToolbarEvent(it)
        }
    }


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (topToolBar, bottomToolbar, bottomToolbarExtension, editorBox, editorBoxBgStretched, textInputView) = createRefs()

        AnimatedToolbarContainer(
            toolbarVisible = showTextEditor.not() && toolbarVisible,
            modifier = topToolbarModifier(topToolBar)
        ) {
            TextModeTopToolbar(
                modifier = Modifier,
                toolbarHeight = topToolbarHeight,
                onCloseClicked = onCloseClickedLambda,
                onDoneClicked = onDoneClickedLambda
            )
        }

        val bitmap = immutableBitmap.bitmap
        val aspectRatio = bitmap.let {
            bitmap.width.toFloat() / bitmap.height.toFloat()
        }
        ScreenshotBox(
            modifier = Modifier
                .constrainAs(editorBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .padding(top = topToolbarHeight, bottom = bottomToolbarHeight)
                .aspectRatio(aspectRatio)
                .clipToBounds(),
            screenshotState = screenshotState
        ) {

            BlurBitmapBackground(
                modifier = Modifier.fillMaxSize(),
                imageBitmap = bitmap.asImageBitmap(),
                shouldBlur = showTextEditor,
                contentScale = if (showTextEditor.not()) ContentScale.Fit else ContentScale.Crop,
                blurRadius = 15,
                onBgClicked = onBgClickedLambda
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (showTextEditor.not()) {
                    DrawAllTransformableViews(
                        centerAlignModifier = Modifier.align(Alignment.Center),
                        transformableViewsList = state.transformableViewStateList,
                        onTransformableBoxEvent = viewModel::onTransformableBoxEvent
                    )
                }
            }

        }

        Box(
            modifier = Modifier
                .constrainAs(editorBoxBgStretched) {
                    top.linkTo(topToolBar.bottom)
                    bottom.linkTo(bottomToolbar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clipToBounds()
        ) {


            if (showTextEditor.not()) {
                BorderForSelectedViews(
                    centerAlignModifier = Modifier.align(Alignment.Center),
                    transformableViewsList = state.transformableViewStateList,
                    onTransformableBoxEvent = viewModel::onTransformableBoxEvent
                )
            }

        }

        AnimatedVisibility(
            visible = showTextEditor,
            modifier = Modifier.constrainAs(textInputView) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TextEditorLayout(
                modifier = Modifier,
                initialEditorState = viewModel.initialTextEditorState,
                onDoneClicked = onDoneClickedTextEditorLambda,
                onClosedClicked = onCloseClickedTextEditorLambda,
            )
        }


        AnimatedToolbarContainer(
            toolbarVisible = showTextEditor.not() && toolbarVisible,
            modifier = bottomToolbarModifier(bottomToolbar)
        ) {
            BottomToolBarStatic(
                modifier = Modifier.fillMaxWidth(),
                toolbarItems = bottomToolbarItems,
                toolbarHeight = bottomToolbarHeight,
                selectedItem = selectedTool,
                onEvent = onBottomToolbarEventLambda
            )
        }

        val emptyLambda = remember { {} }

        AnimatedVisibility(
            visible = state.showBottomToolbarExtension,
            modifier = Modifier
                .constrainAs(bottomToolbarExtension) {
                    bottom.linkTo(bottomToolbar.top)
                    width = Dimension.matchParent
                }
                .clickable(onClick = emptyLambda), /* added clickable{} to avoid triggering touchEvent in DrawingCanvas when clicking anywhere on toolbarExtension */
            enter = AnimUtils.toolbarExtensionExpandAnim(),
            exit = AnimUtils.toolbarExtensionCollapseAnim()

        ) {
            TextModeToolbarExtension(
                modifier = Modifier.fillMaxWidth(),
                bottomToolbarItemState = state.selectedTool,
                onEvent = viewModel::onTextModeToolbarExtensionEvent
            )
        }
    }
}