package com.thezayin.editor.editor.presentation

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.thezayin.editor.editor.domain.repository.ImageRepository
import com.thezayin.editor.editor.presentation.components.ConfirmationDialog
import com.thezayin.editor.editor.presentation.components.EditorBottomBar
import com.thezayin.editor.editor.presentation.components.EditorTopToolBar
import com.thezayin.editor.editor.presentation.components.ImageContent
import com.thezayin.editor.editor.presentation.components.UndoRedoStack
import com.thezayin.editor.editor.presentation.event.EditorBottomBarEvent
import com.thezayin.editor.editor.presentation.event.EditorEvent
import com.thezayin.editor.editor.presentation.event.EditorUiEvent
import com.thezayin.editor.editor.presentation.state.EditorToolbarState
import com.thezayin.editor.editor.presentation.utils.EditorUtils
import com.thezayin.editor.utils.ImmutableList
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@Composable
fun EditorScreen(
    goToCropModeScreen: () -> Unit,
    goToDrawModeScreen: () -> Unit,
    goToTextModeScreen: () -> Unit,
    goToEffectsModeScreen: () -> Unit,
    goToMainScreen: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: EditorViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val imageRepository: ImageRepository = koinInject()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EditorUiEvent.ShowToast -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()

                is EditorUiEvent.ShareImage -> {
                    val bitmap = event.bitmap
                    coroutineScope.launch {
                        val imgFile =
                            File(context.cacheDir, "shared_image_${System.currentTimeMillis()}.jpg")
                        try {
                            imageRepository.saveBitmap(bitmap, imgFile)
                            val uri = imageRepository.getUriForFile(imgFile)
                            if (uri != null) {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    type = "image/*"
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share Image"
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    "Image shared successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to share image.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to share image.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                is EditorUiEvent.SaveImage -> {
                    val bitmap = event.bitmap
                    coroutineScope.launch {
                        val imgFile =
                            File(context.filesDir, "edited_image_${System.currentTimeMillis()}.jpg")
                        try {
                            imageRepository.saveBitmap(bitmap, imgFile)
                            Toast.makeText(context, "Image saved successfully.", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to save image.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
    BackHandler(enabled = true) { viewModel.onEvent(EditorEvent.Close) }

    Scaffold(
        containerColor = colorResource(id = R.color.black),
        topBar = {
            EditorTopToolBar(
                modifier = Modifier.fillMaxWidth(),
                onCloseClicked = { viewModel.onEvent(EditorEvent.Close) },
                onSaveClicked = { viewModel.onEvent(EditorEvent.Save) },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(id = R.color.black)
                    )
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(10.sdp)
                        .background(colorResource(id = R.color.black))
                )
                UndoRedoStack(
                    modifier = Modifier.fillMaxWidth(),
                    undoEnabled = state.canUndo,
                    redoEnabled = state.canRedo,
                    onUndo = { viewModel.onEvent(EditorEvent.Undo) },
                    onRedo = { viewModel.onEvent(EditorEvent.Redo) },
                )
                Spacer(
                    modifier = Modifier
                        .padding(10.sdp)
                        .background(colorResource(id = R.color.black))
                )
                EditorBottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    toolbarItems = ImmutableList(EditorUtils.getDefaultBottomToolbarItemsList()),
                    onEvent = { event ->
                        if (event is EditorBottomBarEvent.OnItemClicked) {
                            when (event.toolbarItem) {
                                EditorToolbarState.CropMode -> goToCropModeScreen()
                                EditorToolbarState.DrawMode -> goToDrawModeScreen()
                                EditorToolbarState.TextMode -> goToTextModeScreen()
                                EditorToolbarState.EffectsMode -> goToEffectsModeScreen()
                            }
                        }
                    }
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colorResource(id = R.color.black))
            ) {
                ImageContent(
                    bitmap = state.currentBitmap,
                    modifier = Modifier
                        .fillMaxSize()

                )
            }

            if (state.showExitConfirmation) {
                ConfirmationDialog(
                    title = stringResource(id = R.string.exit_editor_title),
                    text = stringResource(id = R.string.exit_editor_message),
                    onConfirm = {
                        viewModel.onEvent(EditorEvent.ConfirmExit)
                        goToMainScreen()
                    },
                    onDismiss = {
                        viewModel.onEvent(EditorEvent.DismissExit)
                    }
                )
            }
        }
    )
}