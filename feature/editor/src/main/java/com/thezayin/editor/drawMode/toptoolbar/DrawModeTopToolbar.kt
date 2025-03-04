package com.thezayin.editor.drawMode.toptoolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawModeTopToolBar(
    modifier: Modifier,
    undoEnabled: Boolean = false,
    redoEnabled: Boolean = false,
    doneEnabled: Boolean = false,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onCloseClicked: () -> Unit,
    onDoneClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Close Button
        IconButton(
            onClick = onCloseClicked,
            enabled = true
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // Undo and Redo Buttons
        Row {
            IconButton(
                onClick = onUndo,
                enabled = undoEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = if (undoEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.3f
                    )
                )
            }

            IconButton(
                onClick = onRedo,
                enabled = redoEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Redo,
                    contentDescription = "Redo",
                    tint = if (redoEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.3f
                    )
                )
            }
        }

        // Done Button
        IconButton(
            onClick = onDoneClicked,
            enabled = doneEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Done",
                tint = if (doneEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.3f
                )
            )
        }
    }
}
