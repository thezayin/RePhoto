package com.thezayin.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.thezayin.domain.model.Image
import com.thezayin.values.R

@Composable
fun ImageItem(
    image: Image,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Ensures square cells
            .background(Color.Gray)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = image.uri,
                placeholder = painterResource(id = R.drawable.ic_placeholder), // Ensure you have this drawable
                error = painterResource(id = R.drawable.ic_placeholder) // Ensure you have this drawable
            ),
            contentDescription = image.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}