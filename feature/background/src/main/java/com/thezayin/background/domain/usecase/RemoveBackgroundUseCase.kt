package com.thezayin.background.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.thezayin.background.domain.repository.BackgroundRemovalRepository

/**
 * Use case that removes the background from the base image.
 */
class RemoveBackgroundUseCase(
    private val backgroundRemovalRepository: BackgroundRemovalRepository
) {
    suspend operator fun invoke(baseImage: Uri): Bitmap? {
        // Delegate to the repository
        return backgroundRemovalRepository.removeBackground(baseImage)
    }

    /**
     * Retrieves the original bitmap from the given URI.
     * @param uri The URI of the image.
     * @return The original Bitmap, or null if retrieval fails.
     */
    suspend fun getOriginalBitmap(uri: Uri): Bitmap? {
        return backgroundRemovalRepository.getOriginalBitmap(uri)
    }
}
