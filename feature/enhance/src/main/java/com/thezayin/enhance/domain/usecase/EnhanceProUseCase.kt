package com.thezayin.enhance.domain.usecase
import com.thezayin.enhance.domain.model.Image
import com.thezayin.enhance.domain.repository.ImageEnhancementRepository
interface EnhanceProUseCase {
    suspend operator fun invoke(image: Image): Image
}
class EnhanceProUseCaseImpl(
    private val repository: ImageEnhancementRepository
) : EnhanceProUseCase {
    override suspend fun invoke(image: Image): Image {
        return repository.enhancePro(image)
    }
}