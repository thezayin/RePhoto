package com.thezayin.background.domain.usecase

import android.graphics.Bitmap
import com.thezayin.background.domain.repository.BackgroundBlurRepository

/**
 * Use case for applying edge smoothing to the blurred image.
 *
 * @param repository The [BackgroundBlurRepository] to interact with.
 */
class ApplySmoothingUseCase(
    private val repository: BackgroundBlurRepository
) {
    suspend operator fun invoke(smoothness: Int): Bitmap? {
        return repository.applyEdgeSmoothing(smoothness)
    }
}
