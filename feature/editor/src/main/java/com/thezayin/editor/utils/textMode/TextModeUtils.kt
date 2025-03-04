package com.thezayin.editor.utils.textMode

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.transformableViews.TransformableTextBox
import com.thezayin.editor.transformableViews.base.TransformableBoxEvents
import com.thezayin.editor.transformableViews.base.TransformableBoxState
import com.thezayin.editor.transformableViews.base.TransformableTextBoxState

object TextModeUtils {
    @Composable
    fun getColorsForTextField(
        cursorColor: Color
    ): TextFieldColors {
        return TextFieldDefaults.colors(
            cursorColor = cursorColor,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    }

    @Composable
    fun DrawAllTransformableViews(
        centerAlignModifier: Modifier,
        transformableViewsList: ArrayList<TransformableBoxState>,
        onTransformableBoxEvent: (event: TransformableBoxEvents) -> Unit
    ) {
        transformableViewsList.forEach { mViewState ->
            key(mViewState.id) {
                when (mViewState) {
                    is TransformableTextBoxState -> {
                        TransformableTextBox(
                            modifier = centerAlignModifier,
                            viewState = mViewState,
                            onEvent = onTransformableBoxEvent
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BorderForSelectedViews(
        centerAlignModifier: Modifier,
        transformableViewsList: ArrayList<TransformableBoxState>,
        onTransformableBoxEvent: (event: TransformableBoxEvents) -> Unit
    ) {
        transformableViewsList
            .filter { it.isSelected }
            .forEach { mViewState ->
                key(mViewState.id) {
                    when (mViewState) {
                        is TransformableTextBoxState -> {
                            TransformableTextBox(
                                modifier = centerAlignModifier,
                                viewState = mViewState,
                                showBorderOnly = true,
                                onEvent = onTransformableBoxEvent
                            )
                        }
                    }
                }
            }
    }

    fun getBottomToolbarItemsList(
        selectedViewState: TransformableBoxState?,
        selectedItem: BottomToolbarItemState? = null
    ): ArrayList<BottomToolbarItemState> {

        val toolbarItems: ArrayList<BottomToolbarItemState> = arrayListOf()
        toolbarItems.add(BottomToolbarItemState.AddItemState)
        if (selectedViewState == null || selectedViewState !is TransformableTextBoxState) {
            return toolbarItems
        }

        val textFormatToolItem: BottomToolbarItemState.TextFormat =
            if (selectedItem is BottomToolbarItemState.TextFormat)
                selectedItem else getTextFormat(selectedViewState)
        toolbarItems.add(textFormatToolItem)

        val textFontFamilyToolItem: BottomToolbarItemState.TextFontFamily =
            if (selectedItem is BottomToolbarItemState.TextFontFamily)
                selectedItem else getTextFontFamily(selectedViewState)
        toolbarItems.add(textFontFamilyToolItem)
        return toolbarItems
    }

    private fun getTextFormat(viewState: TransformableTextBoxState): BottomToolbarItemState.TextFormat {
        return BottomToolbarItemState.TextFormat(
            textStyleAttr = viewState.textStyleAttr,
            textCaseType = viewState.textCaseType,
            textAlign = viewState.textAlign
        )
    }

    private fun getTextFontFamily(viewState: TransformableTextBoxState): BottomToolbarItemState.TextFontFamily {
        return BottomToolbarItemState.TextFontFamily(textFontFamily = viewState.textFontFamily)
    }

    val DEFAULT_TEXT_ALIGN = TextAlign.Center

    fun getTextAlignOptions() = arrayListOf(
        TextAlign.Start,
        TextAlign.Center,
        TextAlign.End
    )

    fun isTextModeItem(toolbarItem: BottomToolbarItemState): Boolean {
        return toolbarItem is BottomToolbarItemState.AddItemState ||
                toolbarItem is BottomToolbarItemState.TextFormat ||
                toolbarItem is BottomToolbarItemState.TextFontFamily
    }

    fun getNewSelectedToolItem(
        toolbarItems: ArrayList<BottomToolbarItemState>,
        prevSelectedTool: BottomToolbarItemState
    ): BottomToolbarItemState {
        val newSelectedTool = when (prevSelectedTool) {
            is BottomToolbarItemState.TextFormat -> toolbarItems.find { it is BottomToolbarItemState.TextFormat }
            is BottomToolbarItemState.TextFontFamily -> toolbarItems.find { it is BottomToolbarItemState.TextFontFamily }
            else -> BottomToolbarItemState.NONE
        }
        return newSelectedTool ?: BottomToolbarItemState.NONE
    }

    @Composable
    fun getDefaultEditorTextStyle(): TextStyle = MaterialTheme.typography.headlineMedium
}