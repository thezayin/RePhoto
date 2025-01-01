package com.thezayin.background.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.thezayin.background.domain.repository.ImageRepository

class SaveImageUseCase(private val imageRepository: ImageRepository) {
    suspend operator fun invoke(bitmap: Bitmap, filename: String): Uri? {
        return imageRepository.saveImage(bitmap, filename)
    }
}
