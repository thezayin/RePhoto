package com.thezayin.background.domain.usecase

import com.thezayin.background.domain.model.BlurResult
import com.thezayin.background.domain.repository.BackgroundBlurRepository

/**
 * Use case for applying blur to the background.
 *
 * @param repository The [BackgroundBlurRepository] to interact with.
 */
class ApplyBlurToBackgroundUseCase(
    private val repository: BackgroundBlurRepository
) {
    suspend operator fun invoke(blurRadius: Int): BlurResult? {
        return repository.applyBlurToBackground(blurRadius)
    }
}
