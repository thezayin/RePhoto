package com.thezayin.background_changer.compnent

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGSmoothingIcon(
    isSettingsOpen: Boolean, onToggleSettings: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(30.sdp)
            .clickable { onToggleSettings() },
        shape = CircleShape,
        color = if (isSettingsOpen) colorResource(R.color.light_gray_1) else colorResource(R.color.light_gray_1).copy(
            alpha = 0.5f
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.ic_chart),
                contentDescription = stringResource(id = R.string.content_description_settings),
                modifier = Modifier.size(15.sdp)
            )
        }
    }
}