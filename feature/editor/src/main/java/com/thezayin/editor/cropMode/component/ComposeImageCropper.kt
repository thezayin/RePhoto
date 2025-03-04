// File: com.thezayin.editor.cropMode/CropperView.kt

package com.thezayin.editor.cropMode

import android.net.Uri
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView

@Composable
fun ComposeImageCropper(
    imageUri: Uri,
    onCropComplete: (Uri?) -> Unit
) {
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { context ->
//            CropImageView(context).apply {
//                setImageUriAsync(imageUri)
//                setFixedAspectRatio(false) // Set to true if you want fixed aspect ratios
//                setAspectRatio(1, 1) // Example: Square crop
//                setCropShape(CropImageView.CropShape.RECTANGLE) // or CIRCLE
//                setOnCropImageCompleteListener { view, result ->
//                    onCropComplete(result.uri)
//                }
//            }
//        },
//        update = { cropImageView ->
//            // Update the CropImageView if needed
//        }
//    )
}
