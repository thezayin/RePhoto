package com.thezayin.editor.editor.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.values.R

@Composable
fun EditorTopToolBar(
    modifier: Modifier,
    closeEnabled: Boolean = true,
    saveEnabled: Boolean = false,
    onCloseClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .background(color = colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp)
                .clickable {
                    onCloseClicked()
                },
            contentDescription = null,
            imageVector = Icons.Default.ArrowBack,
            colorFilter = ColorFilter.tint(
                color = if (closeEnabled) {
                    colorResource(R.color.white)
                } else colorResource(R.color.greyish)
            )
        )

        Spacer(modifier = Modifier.weight(1f))


        /**
         * Tool Item: SAVE
         */
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp)
                .clickable {
                    onSaveClicked()
                },
            contentDescription = null,
            imageVector = Icons.Default.SaveAlt,
            colorFilter = ColorFilter.tint(
                color = if (saveEnabled) {
                    MaterialTheme.colorScheme.onBackground
                } else Color.DarkGray
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopToolbar() {

    EditorTopToolBar(
        modifier = Modifier.fillMaxWidth(),
        onCloseClicked = {},
        onSaveClicked = {}
    )
}