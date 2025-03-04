package com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.styleOptions

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.editor.common.toolbar.SelectableToolbarItem

@Composable
fun TextStyleOptions(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    textStyleAttr: TextStyleAttr,
    showDividers: Boolean = false,
    onItemClicked: (newTextStyleAttr: TextStyleAttr) -> Unit
) {

    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectableToolbarItem(
            imageVector = Icons.Default.FormatBold,
            isSelected = textStyleAttr.isBold,
            onClick = {
                onItemClicked(
                    textStyleAttr.copy(isBold = !textStyleAttr.isBold)
                )
            }
        )

        if (showDividers) {
            VerticalDivider(Modifier.height(24.dp))
        }


        SelectableToolbarItem(
            imageVector = Icons.Default.FormatItalic,
            isSelected = textStyleAttr.isItalic,
            onClick = {
                onItemClicked(
                    textStyleAttr.copy(isItalic = !textStyleAttr.isItalic)
                )
            }
        )

        if (showDividers) {
            VerticalDivider(Modifier.height(24.dp))
        }

        val isUnderline = textStyleAttr.textDecoration == TextDecoration.Underline
        SelectableToolbarItem(
            imageVector = Icons.Default.FormatUnderlined,
            isSelected = isUnderline,
            onClick = {
                onItemClicked(
                    textStyleAttr.copy(
                        textDecoration = if (isUnderline) TextDecoration.None else TextDecoration.Underline
                    )
                )
            }
        )

        if (showDividers) {
            VerticalDivider(Modifier.height(24.dp))
        }

        val isStrikeThrough = textStyleAttr.textDecoration == TextDecoration.StrikeThrough
        SelectableToolbarItem(
            imageVector = Icons.Default.FormatStrikethrough,
            isSelected = isStrikeThrough,
            onClick = {
                onItemClicked(
                    textStyleAttr.copy(
                        textDecoration = if (isStrikeThrough) TextDecoration.None else TextDecoration.StrikeThrough
                    )
                )
            }
        )

    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_AlignmentOptions() {
    TextStyleOptions(
        textStyleAttr = TextStyleAttr(
            isBold = true,
            textDecoration = TextDecoration.Underline,
        ),
        onItemClicked = {}
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_AlignmentOptions_FullWidth() {
    TextStyleOptions(
        modifier = Modifier.fillMaxWidth(),
        showDividers = true,
        textStyleAttr = TextStyleAttr(
            isBold = true,
            textDecoration = TextDecoration.Underline
        ),
        onItemClicked = {}
    )
}
