package com.thezayin.background_blur.component

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
import androidx.compose.ui.res.stringResource
import com.thezayin.background_blur.state.BackgroundBlurState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

/**
 * Slider component for adjusting edge smoothness and blur intensity.
 *
 * @param state The current UI state.
 * @param adjustSmoothing Callback invoked when the edge smoothness is adjusted.
 * @param adjustBlurIntensity Callback invoked when the blur intensity is adjusted.
 */
@Composable
fun BlurMenuSliders(
    state: BackgroundBlurState,
    adjustBlurIntensity: (Int) -> Unit,
    adjustSmoothing: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.windowBackground)
        ),
        shape = RoundedCornerShape(16.sdp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.sdp, horizontal = 16.sdp)
        ) {
            var smoothSliderValue by remember { mutableFloatStateOf(state.currentSmoothness.toFloat()) }
            var blurSliderValue by remember { mutableFloatStateOf(state.currentBlurRadius.toFloat()) }

            // Update slider values when state changes
            LaunchedEffect(state.currentSmoothness) {
                smoothSliderValue = state.currentSmoothness.toFloat()
            }

            LaunchedEffect(state.currentBlurRadius) {
                blurSliderValue = state.currentBlurRadius.toFloat()
            }

            Text(
                text = stringResource(
                    id = R.string.edge_smoothness_label,
                    smoothSliderValue.toInt()
                ),
                color = colorResource(R.color.white),
                fontSize = 10.ssp
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Slider(
                colors = SliderDefaults.colors(
                    thumbColor = colorResource(R.color.white),
                    activeTrackColor = colorResource(R.color.white),
                    inactiveTrackColor = colorResource(R.color.dusty_grey)
                ),
                value = smoothSliderValue,
                onValueChange = { value ->
                    smoothSliderValue = value
                },
                onValueChangeFinished = {
                    adjustSmoothing(smoothSliderValue.toInt())
                },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.sdp)
                    .graphicsLayer {
                        scaleY = 0.7f
                    }
            )

            Spacer(modifier = Modifier.height(8.sdp))

            Text(
                text = stringResource(id = R.string.blur_intensity_label, blurSliderValue.toInt()),
                color = colorResource(R.color.white),
                fontSize = 10.ssp
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Slider(
                colors = SliderDefaults.colors(
                    thumbColor = colorResource(R.color.white),
                    activeTrackColor = colorResource(R.color.white),
                    inactiveTrackColor = colorResource(R.color.dusty_grey)
                ),
                value = blurSliderValue,
                onValueChange = { value ->
                    blurSliderValue = value
                },
                onValueChangeFinished = {
                    adjustBlurIntensity(blurSliderValue.toInt())
                },
                valueRange = 0f..25f,
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