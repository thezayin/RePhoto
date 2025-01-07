package com.thezayin.enhance.domain.usecase

import android.graphics.Bitmap
import com.thezayin.enhance.domain.model.EnhancementType
import com.thezayin.enhance.domain.repository.EnhanceRepository

/**
 * Use case for enhancing or deblurring an image.
 *
 * @property repository The repository handling image processing.
 */
class EnhanceImageUseCase(
    private val repository: EnhanceRepository
) {
    /**
     * Processes the [bitmap] based on the specified [type].
     *
     * @param bitmap The original image to process.
     * @param type The type of processing to apply.
     * @return The processed bitmap.
     */
    suspend operator fun invoke(bitmap: Bitmap, type: EnhancementType): Bitmap {
        return repository.processImage(bitmap, type)
    }
}