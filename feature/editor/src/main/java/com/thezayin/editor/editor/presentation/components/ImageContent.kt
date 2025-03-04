// File: com/thezayin.editor.editorScreen/presentation/components/ImageContent.kt

package com.thezayin.editor.editor.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R

@Composable
fun ImageContent(bitmap: Bitmap?, modifier: Modifier = Modifier) {
    if (bitmap != null) {
        val aspectRatio = remember(bitmap) {
            bitmap.width.toFloat() / bitmap.height.toFloat()
        }

        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(aspectRatio)
                    .background(MaterialTheme.colorScheme.background),
                bitmap = bitmap.asImageBitmap(),
                contentScale = ContentScale.Fit,
                contentDescription = "Edited Image"
            )
        }
    } else {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.failed_to_load_image),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
