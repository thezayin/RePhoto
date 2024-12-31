package com.thezayin.background_changer.compnent

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGCompareIcon(
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .size(30.sdp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressStart()
                        tryAwaitRelease()
                        isPressed = false
                        onPressEnd()
                    }
                )
            },
        shape = CircleShape,
        color = if (isPressed) colorResource(R.color.light_gray_1) else colorResource(R.color.light_gray_1).copy(
            alpha = 0.5f
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.ic_compare),
                contentDescription = stringResource(id = R.string.content_description_compare),
                modifier = Modifier
                    .size(15.sdp)
            )
        }
    }
}