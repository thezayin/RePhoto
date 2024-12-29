package com.thezayin.background_remover.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thezayin.background_remover.state.BackgroundRemoverState
import com.thezayin.components.beforeafter.BeforeAfterLayout
import com.thezayin.components.beforeafter.ContentOrder
import ir.kaaveh.sdpcompose.sdp

@Composable
fun RemoverBeforeAfter(
    state: BackgroundRemoverState
) {
    val originalBitmap = state.originalBitmap
    val displayBitmap = state.displayBitmap

    if (originalBitmap == null || displayBitmap == null) {
        return
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(10.sdp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(com.thezayin.values.R.color.black_0_3)
        )
    ) {
        BeforeAfterLayout(
            modifier = Modifier
                .fillMaxSize(),
            triggerAnimationKey = state.triggerAnimationKey,
            enableProgressWithTouch = true,
            enableZoom = false,
            contentOrder = ContentOrder.BeforeAfter,

            beforeContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = state.originalBitmap.asImageBitmap(),
                        contentDescription = "Before Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            },
            afterContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AfterImageWithTemplate(
                        removedBgBitmap = state.displayBitmap,
                        contentDescription = "After Image with Template"
                    )
                }
            },
            beforeLabel = {
                BeforeLabel()
            },
            afterLabel = {
                AfterLabel()
            }
        )
    }
}