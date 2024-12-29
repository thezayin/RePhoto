package com.thezayin.background_remover.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun AfterImageWithTemplate(
    removedBgBitmap: Bitmap,
    templatePainter: Painter = painterResource(id = com.thezayin.values.R.drawable.bg_png),
    contentDescription: String = "After Image with Template"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Image(
            painter = templatePainter,
            contentDescription = "Template Background",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(removedBgBitmap.width.toFloat() / removedBgBitmap.height),
            contentScale = ContentScale.FillBounds
        )
        Image(
            bitmap = removedBgBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
