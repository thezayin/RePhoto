package com.thezayin.background_blur.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R

/**
 * Top app bar for the Background Blur screen, containing back and download actions.
 *
 * @param onBackClicked Callback invoked when the back button is clicked.
 * @param onDownloadClicked Callback invoked when the download button is clicked.
 * @param isDownloadEnabled Flag to enable or disable the download button based on state.
 * @param modifier Modifier for styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurTopBar(
    onBackClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isDownloadEnabled: Boolean,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.black)
        ),
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = colorResource(R.color.white)
                )
            }
        },
        actions = {
            IconButton(onClick = onDownloadClicked, enabled = isDownloadEnabled) {
                Icon(
                    tint = if (isDownloadEnabled) colorResource(R.color.white) else colorResource(R.color.dusty_grey),
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = "Download"
                )
            }
        },
        modifier = modifier
    )
}
