package com.thezayin.editor.cropMode

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.thezayin.editor.cropMode.cropperOptions.CropperOption
import com.thezayin.editor.cropMode.cropperOptions.CropperOptionsFullWidth
import com.thezayin.editor.cropMode.event.CropScreenEvent
import com.thezayin.editor.utils.cropMode.CropModeUtils
import com.thezayin.editor.utils.defaultErrorToast
import com.thezayin.editor.utils.other.anim.AnimUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropperScreen(
    viewModel: CropViewModel = koinInject(),
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val backgroundColor = MaterialTheme.colorScheme.background

    val topToolbarHeight = 56.dp
    val bottomToolbarHeight = 72.dp // Adjust as needed

    var toolbarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        toolbarVisible = true
    }

    var shouldCrop by remember { mutableStateOf(false) }
    var showCropRatioDialog by remember { mutableStateOf(false) }
    val cropperOptionsList = remember { CropModeUtils.getCropperOptionsList() }
    var selectedCropOption by remember { mutableIntStateOf(0) }
    var cropImageOptions by remember {
        mutableStateOf(CropImageOptions())
    }

    BackHandler {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            toolbarVisible = false
            delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
            onBackPressed()
        }
    }

    // Correctly collect currentBitmap from ViewModel's state
    val currentBitmap by viewModel.state
        .map { it.currentBitmap }
        .collectAsState(initial = null)

    // Define handleCropResult correctly
    val handleCropResult: (Bitmap) -> Unit = remember {
        { bitmap: Bitmap ->
            lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                toolbarVisible = false
                delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
                // Dispatch the CropCompleted event to the CropViewModel
                viewModel.onEvent(CropScreenEvent.CropCompleted(bitmap))
                onBackPressed()
            }
        }
    }

    val cropCompleteListener = remember {
        OnCropImageCompleteListener { _, result ->
            result.bitmap?.let {
                handleCropResult(it)
            } ?: context.defaultErrorToast()
        }
    }

    // Define onCropOptionItemClicked correctly
    val onCropOptionItemClicked: (Int, CropperOption) -> Unit = remember {
        { position: Int, cropOption: CropperOption ->
            selectedCropOption = position
            when (cropOption.aspectRatioX) {
                -1f -> {
                    cropImageOptions = cropImageOptions.copy(
                        fixAspectRatio = false,
                        aspectRatioX = 1,
                        aspectRatioY = 1
                    )
                }

                -2f -> {
                    showCropRatioDialog = true
                }

                else -> {
                    cropImageOptions = cropImageOptions.copy(
                        fixAspectRatio = true,
                        aspectRatioX = cropOption.aspectRatioX.toInt(),
                        aspectRatioY = cropOption.aspectRatioY.toInt()
                    )
                }
            }
        }
    }

    // Snackbar Host State
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe UI Events from CropViewModel and show Snackbars
    val cropUiEvent by viewModel.uiEvent.collectAsState(initial = "")
    if (cropUiEvent.isNotEmpty()) {
        LaunchedEffect(cropUiEvent) {
            snackbarHostState.showSnackbar(cropUiEvent)
            // Optionally, clear the event after handling
        }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = toolbarVisible) {
                TopAppBar(
                    title = { Text(text = "Crop Image") },
                    navigationIcon = {
                        IconButton(onClick = {
                            lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                toolbarVisible = false
                                delay(AnimUtils.TOOLBAR_COLLAPSE_ANIM_DURATION_FAST.toLong())
                                onBackPressed()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            shouldCrop = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done"
                            )
                        }
                    },
                    modifier = Modifier.height(topToolbarHeight)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = toolbarVisible) {
                BottomAppBar(
                    modifier = Modifier.height(bottomToolbarHeight)
                ) {
                    CropperOptionsFullWidth(
                        modifier = Modifier.fillMaxWidth(),
                        cropperOptionList = cropperOptionsList,
                        selectedIndex = selectedCropOption,
                        onItemClicked = onCropOptionItemClicked
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
            ) {
                if (currentBitmap != null) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            CropImageView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                setImageBitmap(currentBitmap)
                                setImageCropOptions(cropImageOptions)
                                setOnCropImageCompleteListener(cropCompleteListener)
                            }
                        },
                        update = { cropImageView ->
                            if (shouldCrop) {
                                cropImageView.croppedImageAsync()
                                shouldCrop = false // Reset the flag here
                            }
                            cropImageView.setImageCropOptions(cropImageOptions)
                        }
                    )
                } else {
                    Text(
                        text = "No image to crop.",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

            }
        }
    )
}