package com.thezayin.background_remover.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import com.thezayin.background_remover.state.BackgroundRemoverState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun RemoverSmoothingSlider(
    state: BackgroundRemoverState,
    adjustSmoothing: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.windowBackground)
        ),
        shape = RoundedCornerShape(
            topStart = 16.sdp,
            topEnd = 16.sdp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.sdp,
                    horizontal = 20.sdp
                )
        ) {
            var sliderValue by remember { mutableFloatStateOf(state.currentBlurRadius.toFloat()) }

            LaunchedEffect(state.currentBlurRadius) {
                sliderValue = state.currentBlurRadius.toFloat()
            }

            Text(
                text = "Smoothing Corners: ${sliderValue.toInt()}",
                color = colorResource(R.color.white),
                fontSize = 10.ssp
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Slider(
                colors = SliderDefaults.colors(
                    thumbColor = colorResource(R.color.white),
                    activeTrackColor = colorResource(R.color.white),
                    inactiveTrackColor = colorResource(R.color.dusty_grey)
                ),
                value = sliderValue,
                onValueChange = { value ->
                    sliderValue = value
                },
                onValueChangeFinished = {
                    adjustSmoothing(sliderValue.toInt())
                },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.sdp)
                    .graphicsLayer {
                        scaleY = 0.7f
                    }
            )
        }
    }
}