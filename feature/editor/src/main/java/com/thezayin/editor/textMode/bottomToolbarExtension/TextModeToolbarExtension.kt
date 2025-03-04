package com.thezayin.editor.textMode.bottomToolbarExtension

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.textMode.bottomToolbarExtension.fontFamilyOptions.FontFamilyOptions
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.alignmentOptions.TextAlignOptions
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.caseOptions.TextCaseOptions
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.caseOptions.TextCaseType
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.styleOptions.TextStyleAttr
import com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.styleOptions.TextStyleOptions
import com.thezayin.editor.textMode.bottomToolbarExtension.TextModeToolbarExtensionEvent.UpdateTextAlignment
import com.thezayin.editor.textMode.bottomToolbarExtension.TextModeToolbarExtensionEvent.UpdateTextCaseType
import com.thezayin.editor.textMode.bottomToolbarExtension.TextModeToolbarExtensionEvent.UpdateTextFontFamily
import com.thezayin.editor.textMode.bottomToolbarExtension.TextModeToolbarExtensionEvent.UpdateTextStyleAttr
import com.thezayin.editor.utils.drawMode.DrawModeUtils
import com.thezayin.editor.utils.textMode.FontUtils
import com.thezayin.editor.utils.textMode.TextModeUtils

/**
 * Contains additional option for text-mode tools
 */
@Composable
fun TextModeToolbarExtension(
    modifier: Modifier,
    bottomToolbarItemState: BottomToolbarItemState,
    onEvent: (TextModeToolbarExtensionEvent) -> Unit
) {

    when (bottomToolbarItemState) {
        is BottomToolbarItemState.TextFormat -> {
            TextModeToolbarExtTextFormat(
                modifier = modifier,
                bottomToolbarItemState = bottomToolbarItemState,
                onEvent = onEvent
            )
        }

        is BottomToolbarItemState.TextFontFamily -> {
            TextModeToolbarExtFont(
                modifier = modifier,
                bottomToolbarItemState = bottomToolbarItemState,
                onEvent = onEvent
            )
        }

        else -> {}
    }

}

@Composable
fun TextModeToolbarExtFont(
    modifier: Modifier = Modifier,
    bottomToolbarItemState: BottomToolbarItemState.TextFontFamily,
    onEvent: (TextModeToolbarExtensionEvent) -> Unit
) {

    FontFamilyOptions(
        modifier = modifier,
        selectedFontFamily = bottomToolbarItemState.textFontFamily,
        onItemClicked = {
            onEvent(UpdateTextFontFamily(it))
        }
    )
}


@Composable
fun TextModeToolbarExtTextFormat(
    modifier: Modifier = Modifier,
    bottomToolbarItemState: BottomToolbarItemState.TextFormat,
    onEvent: (TextModeToolbarExtensionEvent) -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.Transparent)
    ) {

        TextStyleOptions(
            modifier = Modifier.fillMaxWidth(),
            textStyleAttr = bottomToolbarItemState.textStyleAttr,
            showDividers = true,
            onItemClicked = {
                onEvent(
                    UpdateTextStyleAttr(textStyleAttr = it)
                )
            }
        )

        HorizontalDivider(Modifier.padding(4.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextCaseOptions(
                modifier = Modifier.weight(1f),
                selectedTextCase = bottomToolbarItemState.textCaseType,
                onItemClicked = {
                    onEvent(
                        UpdateTextCaseType(textCaseType = it)
                    )
                }
            )

            VerticalDivider(Modifier.height(24.dp))

            TextAlignOptions(
                modifier = Modifier.weight(1f),
                selectedAlignment = bottomToolbarItemState.textAlign,
                onItemClicked = { _, textAlign ->
                    onEvent(
                        UpdateTextAlignment(textAlignment = textAlign)
                    )
                }
            )
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTextModeToolbarExtension() {
    val bottomTools = DrawModeUtils.getDefaultBottomToolbarItemsList()
    TextModeToolbarExtension(
        modifier = Modifier.fillMaxWidth(),
        bottomToolbarItemState = BottomToolbarItemState.TextFormat(
            textStyleAttr = TextStyleAttr(isBold = true),
            textCaseType = TextCaseType.DEFAULT,
            textAlign = TextModeUtils.DEFAULT_TEXT_ALIGN
        ),
        onEvent = {}
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewToolbarExtFontFamily() {
    val bottomTools = DrawModeUtils.getDefaultBottomToolbarItemsList()
    TextModeToolbarExtension(
        modifier = Modifier.fillMaxWidth(),
        bottomToolbarItemState = BottomToolbarItemState.TextFontFamily(
            textFontFamily = FontUtils.DefaultFontFamily
        ),
        onEvent = {}
    )
}