package com.thezayin.background.domain.usecase

import android.graphics.Bitmap
import com.thezayin.background.domain.repository.BackgroundRemovalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case that applies smoothing to the edges of an image.
 */
class SmoothImageUseCase(
    private val backgroundRemovalRepository: BackgroundRemovalRepository
) {
    /**
     * Applies smoothing to the provided bitmap based on the smoothing value.
     * @param bitmap The bitmap to smooth.
     * @param value Smoothing value from 0 (no smoothing) to 100 (max smoothing).
     * @return The smoothed bitmap.
     */
    suspend operator fun invoke(bitmap: Bitmap, value: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            backgroundRemovalRepository.smoothBitmap(bitmap, value)
        }
    }
}