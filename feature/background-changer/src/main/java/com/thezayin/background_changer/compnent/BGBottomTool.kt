package com.thezayin.background_changer.compnent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BGBottomTool(
    imageRes: Int,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .size(50.sdp)
            .background(color = colorResource(R.color.black_alpha_40))
            .clip(RoundedCornerShape(8.sdp))
            .border(1.sdp, color = colorResource(R.color.white), shape = RoundedCornerShape(8.sdp))
            .clickable(enabled = isEnabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            modifier = Modifier
                .size(20.sdp)
                .padding(bottom = 2.sdp)
        )
        Text(
            text = text,
            fontSize = 8.ssp,
            color = colorResource(R.color.white).copy(alpha = if (isEnabled) 1f else 0.5f)
        )
    }
}