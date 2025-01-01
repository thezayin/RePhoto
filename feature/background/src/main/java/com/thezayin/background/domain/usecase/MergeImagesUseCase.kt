package com.thezayin.background.domain.usecase

import android.graphics.Bitmap
import com.thezayin.background.domain.repository.ImageRepository

class MergeImagesUseCase(private val imageRepository: ImageRepository) {
    suspend operator fun invoke(
        background: Bitmap?,
        processed: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        rotation: Float
    ): Bitmap? {
        return imageRepository.mergeImages(
            background = background,
            processed = processed,
            scale = scale,
            offsetX = offsetX,
            offsetY = offsetY,
            rotation = rotation
        )
    }
}
