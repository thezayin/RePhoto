package com.thezayin.background_changer.compnent

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGDemo(
    imageRes: Int,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .size(50.sdp)
            .clip(RoundedCornerShape(8.sdp))
            .border(1.sdp, color = colorResource(R.color.white), shape = RoundedCornerShape(8.sdp))
            .clickable(enabled = isEnabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}