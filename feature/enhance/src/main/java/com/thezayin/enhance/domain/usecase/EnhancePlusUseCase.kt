package com.thezayin.enhance.domain.usecase
import com.thezayin.enhance.domain.model.Image
import com.thezayin.enhance.domain.repository.ImageEnhancementRepository

interface EnhancePlusUseCase {
    suspend operator fun invoke(image: Image): Image
}
class EnhancePlusUseCaseImpl(
    private val repository: ImageEnhancementRepository
) : EnhancePlusUseCase {
    override suspend fun invoke(image: Image): Image {
        return repository.enhancePlus(image)
    }
}