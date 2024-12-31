package com.thezayin.background_changer.compnent

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.thezayin.framework.extension.convertDrawableToBitmap
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BGOptions(
    onSelectGallery: () -> Unit,
    onSelectBackground: (Bitmap) -> Unit,
    onMore: () -> Unit,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val backgroundDrawableRes = listOf(
        R.drawable.bg_blue,
        R.drawable.bg_white,
        R.drawable.bg_royal
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.sdp),
        horizontalArrangement = Arrangement.spacedBy(4.sdp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            BGBottomTool(
                imageRes = R.drawable.ic_gallery,
                text = stringResource(id = R.string.label_gallery),
                onClick = { onSelectGallery() },
                isEnabled = !isLoading
            )
        }

        items(backgroundDrawableRes) { drawableRes ->
            BGDemo(
                imageRes = drawableRes,
                text = stringResource(id = R.string.label_background),
                onClick = {
                    val bitmap = convertDrawableToBitmap(context, drawableRes)
                    if (bitmap != null) {
                        onSelectBackground(bitmap)
                    }
                },
                isEnabled = !isLoading
            )
        }

        item {
            BGBottomTool(
                imageRes = R.drawable.ic_dots,
                text = stringResource(id = R.string.label_more),
                onClick = { onMore() },
                isEnabled = !isLoading
            )
        }
    }
}
