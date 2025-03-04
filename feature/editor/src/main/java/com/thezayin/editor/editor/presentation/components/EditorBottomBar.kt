package com.thezayin.editor.editor.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.editor.editor.presentation.event.EditorBottomBarEvent
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.editor.presentation.state.EditorToolbarState
import com.thezayin.editor.editor.presentation.utils.EditorUtils
import com.thezayin.editor.utils.ImmutableList
import com.thezayin.editor.utils.defaultTextColor
import com.thezayin.values.R

@Composable
fun EditorBottomBar(
    modifier: Modifier,
    toolbarItems: ImmutableList<EditorToolbarState>,
    selectedItem: BottomToolbarItemState = BottomToolbarItemState.NONE,
    onEvent: (EditorBottomBarEvent) -> Unit
) {

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolbarItems.items.forEachIndexed { _, mToolbarItem ->
            BottomToolbarItem(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                toolbarItem = mToolbarItem,
                isSelected = mToolbarItem == selectedItem,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun BottomToolbarItem(
    modifier: Modifier = Modifier,
    toolbarItem: EditorToolbarState,
    isSelected: Boolean,
    onEvent: (EditorBottomBarEvent) -> Unit
) {
    val labelTextStyle = MaterialTheme.typography.bodySmall.copy(color = defaultTextColor())

    val commonPaddingModifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    var columnModifier = modifier.clickable {
        onEvent(EditorBottomBarEvent.OnItemClicked(toolbarItem))
    }
    if (isSelected) {
        columnModifier = columnModifier
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .padding((0.5).dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.DarkGray)
    }
    columnModifier = columnModifier.then(commonPaddingModifier)


    val (imageVector, labelText) = when (toolbarItem) {

        is EditorToolbarState.CropMode -> Pair(
            Icons.Outlined.Crop,
            stringResource(id = R.string.crop)
        )

        is EditorToolbarState.DrawMode -> Pair(
            Icons.Outlined.Brush,
            stringResource(id = R.string.draw)

        )

        is EditorToolbarState.TextMode -> Pair(
            Icons.Default.TextFields,
            stringResource(id = R.string.text)
        )

        is EditorToolbarState.EffectsMode -> Pair(
            ImageVector.vectorResource(id = R.drawable.ic_effects),
            stringResource(id = R.string.effects)
        )
    }


    Column(
        modifier = columnModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val verticalPaddingBeforeSize = if (labelText.isBlank()) 4.dp else 0.dp
        val imageSize = if (labelText.isBlank()) 32.dp else 28.dp
        Image(
            modifier = Modifier
                .padding(vertical = verticalPaddingBeforeSize)
                .size(imageSize),
            contentDescription = null,
            imageVector = imageVector,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(
            modifier = Modifier.size(
                if (labelText.isNotBlank()) 4.dp else 0.dp
            )
        )

        if (labelText.isNotBlank()) {
            Text(
                style = labelTextStyle,
                text = labelText
            )
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditorScreen_BottomToolbar() {
    val itemsList = EditorUtils.getDefaultBottomToolbarItemsList()
    EditorBottomBar(
        modifier = Modifier.fillMaxWidth(),
        toolbarItems = ImmutableList(itemsList),
        onEvent = {}
    )
}