package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun OnBoardNavButton(
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit = {}
) {
    Button(
        onClick = { onNextClicked() },
        modifier = modifier
            .fillMaxWidth()
            .height(40.sdp),
        shape = RoundedCornerShape(20.sdp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.button_color),
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier)
            Text(
                text = "continue",
                fontSize = 14.ssp,
                color = colorResource(id = R.color.white)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_forward),
                contentDescription = "continue",
                tint = colorResource(id = R.color.white),
                modifier = Modifier.size(25.sdp)
            )
        }
    }
}
