package com.thezayin.background.domain.usecase

import com.thezayin.background.domain.model.BlurResult
import com.thezayin.background.domain.repository.BackgroundBlurRepository

/**
 * Use case for adjusting the blur intensity.
 *
 * @param repository The [BackgroundBlurRepository] to interact with.
 */
class AdjustBlurIntensityUseCase(
    private val repository: BackgroundBlurRepository
) {
    suspend operator fun invoke(newBlurRadius: Int): BlurResult? {
        // Re-apply blur with the new radius
        return repository.applyBlurToBackground(newBlurRadius)
    }
}
