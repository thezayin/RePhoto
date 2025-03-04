package com.thezayin.editor.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.thezayin.editor.utils.defaultTextColor
import com.thezayin.values.R

@Composable
fun ErrorView(
    modifier: Modifier,
    errorText: String = stringResource(R.string.something_went_wrong),
    errorFontStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {

    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = errorText,
            style = errorFontStyle.copy(color = defaultTextColor())
        )
    }

}