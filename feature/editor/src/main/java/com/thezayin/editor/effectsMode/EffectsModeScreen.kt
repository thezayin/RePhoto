@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thezayin.editor.effectsMode

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.thezayin.editor.common.AnimatedToolbarContainer
import com.thezayin.editor.common.LoadingView
import com.thezayin.editor.common.bottomToolbarModifier
import com.thezayin.editor.common.topToolbarModifier
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_EXTRA_LARGE
import com.thezayin.editor.editor.presentation.components.TOOLBAR_HEIGHT_SMALL
import com.thezayin.editor.textMode.topToolbar.TextModeTopToolbar
import com.thezayin.editor.effectsMode.effectsPreview.EffectItem
import com.thezayin.editor.effectsMode.effectsPreview.EffectsPreviewListFullWidth
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import com.thezayin.editor.utils.defaultErrorToast
import com.thezayin.editor.utils.effectsMode.EffectsModeUtils
import com.thezayin.editor.utils.other.anim.AnimUtils
import com.thezayin.editor.utils.other.bitmap.ImmutableBitmap
import com.thezayin.values.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun EffectsModeScreen(
    modifier: Modifier = Modifier,
    immutableBitmap: ImmutableBitmap,
    onDoneClicked: (bitmap: Bitmap) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val viewModel: EffectsModeViewModel = koinInject()
    val state by viewModel.state.collectAsStateWithLifecycle(
        lifecycleOwner = lifeCycleOwner
    )

    val bitmap = immutableBitmap.bitmap
    val currentBitmap = state.filteredBitmap ?: bitmap

    val topToolbarHeight = TOOLBAR_HEIGHT_SMALL
    val bottomToolbarHeight = TOOLBAR_HEIGHT_EXTRA_LARGE

    var toolbarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = bitmap) {
        withContext(Dispatchers.IO) {
            toolbarVisible = true
            delay(AnimUtils.TOOLBAR_EXPAND_ANIM_DURATION_FAST.toLong())
            EffectsModeUtils.getEffectsPreviewList(context, bitmap).onEach {
                viewModel.addToEffectList(
                    effectItems = it,
                    selectInitialBitmap = state.effectsList.isEmpty(),
                )
            }.collect()

        }
    }

    val screenshotState = rememberScreenshotState()

    val onCloseClickedLambda = remember<() -> Unit> {
        {
            lifeCycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                toolbarVisible = false
                delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
                onBackPressed()
            }
        }
    }

    BackHandler {
        onCloseClickedLambda()
    }

    val onDoneClickedLambda = remember<() -> Unit> {
        {
            viewModel.shouldGoToNextScreen = true
            screenshotState.capture()
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

    // TODO effects - Try to use the original bitmap from the effectList instead of taking screenshot
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


    val onEffectItemClicked = remember<(Int, EffectItem) -> Unit> {
        { index, effectItem ->
            viewModel.selectEffect(index)
        }
    }


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        val (topToolBar, screenshotBox, effectsPreviewList) = createRefs()

        AnimatedToolbarContainer(
            toolbarVisible = toolbarVisible,
            modifier = topToolbarModifier(topToolBar)
        ) {
            TextModeTopToolbar(
                modifier = Modifier,
                toolbarHeight = topToolbarHeight,
                onCloseClicked = onCloseClickedLambda,
                onDoneClicked = onDoneClickedLambda
            )
        }
        val aspectRatio = bitmap.let {
            bitmap.width.toFloat() / bitmap.height.toFloat()
        }
        ScreenshotBox(
            modifier = Modifier
                .constrainAs(screenshotBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .padding(top = topToolbarHeight, bottom = bottomToolbarHeight)
                .aspectRatio(aspectRatio)
//                .sharedElement(
//                    state = rememberSharedContentState(key = "centerImage"),
//                    animatedVisibilityScope = animatedVisibilityScope,
//                    boundsTransform = { _, _ ->
//                        tween(300)
//                    },
//                )
            ,
            screenshotState = screenshotState
        ) {

            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = null
            )

        }


        AnimatedToolbarContainer(
            toolbarVisible = toolbarVisible,
            modifier = bottomToolbarModifier(effectsPreviewList)
        ) {
            if (state.effectsList.isEmpty()) {
                LoadingView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomToolbarHeight)
                        .background(Color.Transparent),
                    progressBarSize = 36.dp,
                    progressBarStrokeWidth = 3.dp
                )
            } else {
                EffectsPreviewListFullWidth(
                    modifier = Modifier
                        .background(Color.Transparent)
//                        .padding(vertical = 12.dp)
                    ,
                    toolbarHeight = bottomToolbarHeight,
                    effectsList = state.effectsList,
                    selectedIndex = state.selectedEffectIndex,
                    onItemClicked = onEffectItemClicked
                )
            }
        }

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_EffectsModeScreen() {
    EffectsModeScreen(
        immutableBitmap = ImmutableBitmap(
            ImageBitmap.imageResource(id = R.drawable.placeholder_image_3).asAndroidBitmap()
        ),
        onDoneClicked = {},
        onBackPressed = {}
    )
}