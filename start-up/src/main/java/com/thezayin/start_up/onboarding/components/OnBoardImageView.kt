package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.thezayin.components.beforeafter.BeforeAfterLayout

@Composable
fun OnBoardImageView(
    modifier: Modifier = Modifier, beforeImageRes: Int, afterImageRes: Int, triggerAnimationKey: Int
) {

    Box(modifier = modifier) {
        BeforeAfterLayout(
            modifier = Modifier.fillMaxSize(),
            triggerAnimationKey = triggerAnimationKey,
            beforeContent = {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    painter = painterResource(id = beforeImageRes),
                    contentScale = ContentScale.Crop,
                    contentDescription = "before_image"
                )
            },
            afterContent = {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    painter = painterResource(id = afterImageRes),
                    contentScale = ContentScale.Crop,
                    contentDescription = "after_image"
                )
            },
            enableProgressWithTouch = true,
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.BottomCenter)
            .graphicsLayer {
                alpha = 0.9f
            }
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        Pair(0.3f, Color.Transparent), Pair(1f, Color.Black), Pair(1f, Color.Black)
                    )
                )
            ))
    }
}