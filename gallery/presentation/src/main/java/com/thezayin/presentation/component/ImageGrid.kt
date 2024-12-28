package com.thezayin.presentation.component

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thezayin.domain.model.Image
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ImageGrid(
    images: List<Image>,
    onImageClick: (Uri) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Adjust the number of columns as needed
        modifier = Modifier
            .fillMaxSize()
            .padding(4.sdp),
        contentPadding = PaddingValues(4.sdp),
        horizontalArrangement = Arrangement.spacedBy(4.sdp),
        verticalArrangement = Arrangement.spacedBy(4.sdp)
    ) {
        items(images) { image ->
            ImageItem(image = image, onClick = { onImageClick(image.uri) })
        }
    }
}
