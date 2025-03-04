package com.thezayin.editor.textMode.bottomToolbarExtension.textFormatOptions.caseOptions

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.editor.common.toolbar.SelectableToolbarItem
import com.thezayin.values.R

@Composable
fun TextCaseOptions(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    selectedTextCase: TextCaseType,
    onItemClicked: (TextCaseType) -> Unit
) {

    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectableToolbarItem(imageVector = ImageVector.vectorResource(id = R.drawable.outline_match_case_24),
            isSelected = selectedTextCase == TextCaseType.DEFAULT,
            onClick = {
                onItemClicked(TextCaseType.DEFAULT)
            })

        SelectableToolbarItem(imageVector = ImageVector.vectorResource(id = R.drawable.outline_uppercase_24),
            isSelected = selectedTextCase == TextCaseType.UPPER_CASE,
            onClick = {
                onItemClicked(TextCaseType.UPPER_CASE)
            })

        SelectableToolbarItem(imageVector = ImageVector.vectorResource(id = R.drawable.outline_lowercase_24),
            isSelected = selectedTextCase == TextCaseType.LOWER_CASE,
            onClick = {
                onItemClicked(TextCaseType.LOWER_CASE)
            })

    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_AlignmentOptions() {
    TextCaseOptions(selectedTextCase = TextCaseType.DEFAULT, onItemClicked = {})
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_AlignmentOptions_FullWidth() {
    TextCaseOptions(modifier = Modifier.fillMaxWidth(),
        selectedTextCase = TextCaseType.DEFAULT,
        onItemClicked = {})
}