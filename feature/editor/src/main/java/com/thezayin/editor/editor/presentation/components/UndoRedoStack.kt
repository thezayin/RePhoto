package com.thezayin.editor.editor.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thezayin.values.R

@Composable
fun UndoRedoStack(
    modifier: Modifier,
    undoEnabled: Boolean = false,
    redoEnabled: Boolean = false,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(color = colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(32.dp)
                    .clickable {
                        onUndo()
                    },
                contentDescription = null,
                imageVector = Icons.Default.Undo,
                colorFilter = ColorFilter.tint(
                    color = if (undoEnabled) {
                        colorResource(R.color.white)
                    } else colorResource(R.color.dusty_grey)
                )
            )

            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(32.dp)
                    .clickable {
                        onRedo()
                    },
                contentDescription = null,
                imageVector = Icons.Default.Redo,
                colorFilter = ColorFilter.tint(
                    color = if (redoEnabled) {
                        colorResource(R.color.white)
                    } else colorResource(R.color.dusty_grey)
                )
            )
        }
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp)
                .clickable {

                },
            contentDescription = null,
            imageVector = Icons.Default.Compare,
            colorFilter = ColorFilter.tint(
                color = colorResource(R.color.white)
            )
        )
    }
}