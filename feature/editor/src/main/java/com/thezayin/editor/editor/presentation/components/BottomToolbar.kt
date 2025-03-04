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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.editor.editor.presentation.event.BottomToolbarEvent
import com.thezayin.editor.editor.presentation.state.BottomToolbarItemState
import com.thezayin.editor.transformableViews.base.TransformableTextBoxState
import com.thezayin.editor.utils.ImmutableList
import com.thezayin.editor.utils.defaultTextColor
import com.thezayin.editor.utils.drawMode.DrawModeUtils
import com.thezayin.editor.utils.textMode.TextModeUtils
import com.thezayin.values.R
import java.util.UUID

val TOOLBAR_HEIGHT_SMALL = 48.dp
val TOOLBAR_HEIGHT_MEDIUM = 64.dp
val TOOLBAR_HEIGHT_LARGE = 88.dp
val TOOLBAR_HEIGHT_EXTRA_LARGE = 104.dp

@Composable
fun BottomToolBarStatic(
    modifier: Modifier,
    toolbarItems: ImmutableList<BottomToolbarItemState>,
    toolbarHeight: Dp = TOOLBAR_HEIGHT_MEDIUM,
    selectedItem: BottomToolbarItemState = BottomToolbarItemState.NONE,
    showColorPickerIcon: Boolean = true,
    selectedColor: Color = Color.White,
    onEvent: (BottomToolbarEvent) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(toolbarHeight)
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolbarItems.items.forEachIndexed { index, mToolbarItem ->
            ToolbarItem(
                toolbarItem = mToolbarItem,
                selectedColor = selectedColor,
                showColorPickerIcon = showColorPickerIcon,
                isSelected = mToolbarItem == selectedItem,
                onEvent = onEvent
            )
        }
    }
}


@Composable
fun ToolbarItem(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    showColorPickerIcon: Boolean,
    toolbarItem: BottomToolbarItemState,
    isSelected: Boolean,
    onEvent: (BottomToolbarEvent) -> Unit
) {
    val labelTextStyle = MaterialTheme.typography.bodySmall.copy(color = defaultTextColor())

    val commonPaddingModifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    if (toolbarItem is BottomToolbarItemState.ColorItemState) {
        ColorToolbarItem(
            modifier = modifier.then(commonPaddingModifier),
            selectedColor = selectedColor,
            showColorPickerIcon = showColorPickerIcon,
            colorItem = toolbarItem,
            labelTextStyle = labelTextStyle,
            onEvent = onEvent
        )
        return
    }
    var columnModifier = modifier.clickable {
        onEvent(BottomToolbarEvent.OnItemClicked(toolbarItem))
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
        is BottomToolbarItemState.EraserTool -> Pair(
            ImageVector.vectorResource(id = R.drawable.ic_eraser),
            stringResource(id = R.string.eraser)
        )

        is BottomToolbarItemState.ShapeTool -> Pair(
            Icons.Outlined.Category,
            stringResource(id = R.string.shape)
        )

        is BottomToolbarItemState.BrushTool -> Pair(
            ImageVector.vectorResource(id = R.drawable.ic_stylus_note),
            stringResource(id = R.string.brush)
        )

        is BottomToolbarItemState.PanItemState -> Pair(
            Icons.Outlined.PanTool,
            stringResource(id = R.string.zoom)
        )

        is BottomToolbarItemState.TextFormat -> Pair(
            ImageVector.vectorResource(id = R.drawable.outline_custom_typography_24),
            stringResource(id = R.string.format)
        )

        is BottomToolbarItemState.TextFontFamily -> Pair(
            ImageVector.vectorResource(id = R.drawable.outline_serif_24),
            stringResource(id = R.string.font)
        )

        else -> Pair(
            Icons.Default.AddCircleOutline,
            ""
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

@Composable
fun ColorToolbarItem(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    showColorPickerIcon: Boolean,
    colorItem: BottomToolbarItemState.ColorItemState,
    labelTextStyle: TextStyle,
    onEvent: (BottomToolbarEvent) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (showColorPickerIcon) {
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.ic_color_picker),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        onEvent(BottomToolbarEvent.OnItemClicked(colorItem))
                    }
            )
        } else {
            Image(
                painter = ColorPainter(selectedColor),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .padding(1.dp)
                    .clip(CircleShape)
                    .size(26.dp)
                    .clickable {
                        onEvent(BottomToolbarEvent.OnItemClicked(colorItem))
                    }
            )
        }


        Spacer(modifier = Modifier.size(4.dp))

        Text(
            style = labelTextStyle,
            text = stringResource(id = R.string.color)
        )
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DrawMode_BottomToolbar() {

    val itemsList = DrawModeUtils.getDefaultBottomToolbarItemsList()
    BottomToolBarStatic(
        modifier = Modifier.fillMaxWidth(),
        toolbarItems = ImmutableList(itemsList),
        showColorPickerIcon = true,
        selectedColor = Color.White,
        selectedItem = itemsList[DrawModeUtils.DEFAULT_SELECTED_INDEX],
        onEvent = {}
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TextMode_BottomToolbar() {

    val itemsList = TextModeUtils.getBottomToolbarItemsList(
        selectedViewState = TransformableTextBoxState(
            id = UUID.randomUUID().toString(),
            text = "hello",
            textColor = Color.White,
            textFont = 8.sp,
        )
    )
    BottomToolBarStatic(
        modifier = Modifier.fillMaxWidth(),
        toolbarItems = ImmutableList(itemsList),
        showColorPickerIcon = true,
        selectedColor = Color.White,
        onEvent = {}
    )
}