package com.thezayin.background.domain.usecase

import com.thezayin.background.domain.model.BackgroundImages
import com.thezayin.background.domain.repository.BackgroundRemovalRepository

/**
 * Use case to fetch categorized background images from Firebase Remote Config.
 *
 * @param repository The repository handling data retrieval operations.
 */
class GetBackgroundImagesUseCase(
    private val repository: BackgroundRemovalRepository
) {
    /**
     * Executes the fetch operation for background images.
     *
     * @return An instance of [BackgroundImages] containing categories and their image URLs.
     */
    suspend operator fun invoke(): BackgroundImages {
        return repository.getBackgroundImages()
    }
}
