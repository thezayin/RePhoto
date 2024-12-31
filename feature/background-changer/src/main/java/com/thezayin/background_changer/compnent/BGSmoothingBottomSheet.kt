// SettingsBottomSheetContent.kt
package com.thezayin.background_changer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BGSmoothingBottomSheet(
    currentSmoothing: Float,
    onSmoothingChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = colorResource(R.color.gray_level_3))
            .fillMaxWidth()
            .padding(16.sdp)
    ) {
        Text(
            text = "${stringResource(id = R.string.label_edge_smoothing)}: ${currentSmoothing.toInt()}",
            fontSize = 10.ssp,
            color = colorResource(R.color.white),
        )
        Spacer(modifier = Modifier.height(8.sdp))
        Slider(
            colors = SliderDefaults.colors(
                thumbColor = colorResource(R.color.white),
                activeTrackColor = colorResource(R.color.white),
                inactiveTrackColor = colorResource(R.color.dusty_grey)
            ),
            value = currentSmoothing,
            onValueChange = { onSmoothingChange(it) },
            valueRange = 1f..100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.sdp)
                .graphicsLayer {
                    scaleY = 0.7f
                }
        )
        Spacer(modifier = Modifier.height(25.sdp))
    }
}