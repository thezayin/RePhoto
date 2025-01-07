package com.thezayin.background_blur.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.thezayin.values.R

/**
 * Dialog that prompts the user to choose the image format for saving.
 *
 * @param onDismissRequest Callback invoked when the dialog is dismissed.
 * @param saveAsPng Callback invoked with the user's format choice.
 */
@Composable
fun BlurSaveDialog(
    onDismissRequest: (Boolean) -> Unit,
    saveAsPng: (Boolean) -> Unit,
) {
    AlertDialog(
        containerColor = colorResource(R.color.black_login),
        onDismissRequest = { onDismissRequest(false) },
        title = { Text(text = "Save Image", color = colorResource(R.color.white)) },
        text = {
            Text(
                text = "Choose the format to save your image.",
                color = colorResource(R.color.white)
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                ),
                onClick = {
                    saveAsPng(true)
                    onDismissRequest(false)
                }
            ) {
                Text("Save as PNG", color = colorResource(R.color.black))
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                ),
                onClick = {
                    saveAsPng(false)
                    onDismissRequest(false)
                }) {
                Text("Save as JPG", color = colorResource(R.color.black))
            }
        }
    )
}
