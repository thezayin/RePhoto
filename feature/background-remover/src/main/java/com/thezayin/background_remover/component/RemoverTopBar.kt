package com.thezayin.background_remover.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.thezayin.values.R

/**
 * A composable that represents the top app bar for the Background Remover screen.
 *
 * @param onBackClicked Lambda to be invoked when the back button is clicked.
 * @param onDownloadClicked Lambda to be invoked when the download button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoverTopBar(
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
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