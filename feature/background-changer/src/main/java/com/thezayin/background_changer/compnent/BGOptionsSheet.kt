package com.thezayin.background_changer.compnent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import coil.compose.rememberAsyncImagePainter
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BGOptionsSheet(
    categories: List<String>,
    imagesByCategory: Map<String, List<String>>,
    onImageSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    isLoading: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.gray_level_3))
            .padding(8.sdp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.sdp),
            horizontalArrangement = Arrangement.spacedBy(4.sdp)
        ) {
            items(categories) { category ->
                Text(
                    text = category,
                    modifier = Modifier
                        .padding(horizontal = 4.sdp, vertical = 2.sdp)
                        .clickable(enabled = !isLoading) {
                            selectedCategory = category
                            onCategorySelected(category)
                        },
                    color = if (category == selectedCategory) Color.White else Color.Gray,
                    fontSize = 10.ssp
                )
            }
        }

        val images = imagesByCategory[selectedCategory] ?: emptyList()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.sdp)
        ) {
            items(images) { imageUrl ->
                Box(
                    modifier = Modifier
                        .padding(4.sdp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.sdp))
                        .background(Color.LightGray)
                        .clickable(enabled = !isLoading) {
                            if (!isLoading) {
                                onImageSelected(imageUrl)
                            }
                        }
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.sdp)
                        }
                    }

                    androidx.compose.foundation.Image(
                        contentScale = ContentScale.FillBounds,
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                            onLoading = {},
                            onSuccess = {},
                            onError = {}
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        alignment = Alignment.Center
                    )
                }
            }
        }
    }
}