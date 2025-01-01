package com.thezayin.background_changer.compnent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BGTopBar(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit,
    isApplyEnabled: Boolean = true
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
         containerColor = colorResource(id = R.color.black),
        ),
        title = {
            Spacer(modifier = Modifier.height(0.dp))
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.content_description_back),
                modifier = Modifier
                    .padding(start = 10.sdp)
                    .size(20.sdp)
                    .clickable(onClick = onBackClick)
            )
        },
        actions = {
            Text(
                text = stringResource(id = R.string.apply),
                fontSize = 12.ssp,
                color = if (isApplyEnabled) colorResource(id = R.color.button_color) else colorResource(
                    id = R.color.greyish
                ),
                modifier = Modifier
                    .padding(end = 10.sdp)
                    .clickable(enabled = isApplyEnabled, onClick = onApplyClick)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}
