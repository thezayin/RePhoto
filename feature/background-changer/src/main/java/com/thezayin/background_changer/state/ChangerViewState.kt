package com.thezayin.background_changer.state

import android.graphics.Bitmap
import com.thezayin.background.domain.model.BackgroundImageCategory

data class ChangerViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val processedImageOriginal: Bitmap? = null,
    val processedImage: Bitmap? = null,
    val baseImage: Bitmap? = null,
    val backgroundImage: Bitmap? = null,
    val isShowingBaseImage: Boolean = false,
    val statusText: String = "",
    val isError: Boolean = false,
    val backgroundImages: List<BackgroundImageCategory> = emptyList(),
    val imageScale: Float = 1.0f,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageRotation: Float = 0f,
    val smoothingValue: Float = 0f,
    val navigateToNextScreen: Boolean = false
)
