package com.thezayin.enhance.domain.usecase
import com.thezayin.enhance.domain.model.Image
import com.thezayin.enhance.domain.repository.ImageEnhancementRepository
interface EnhanceUseCase {
    suspend operator fun invoke(image: Image): Image
}
class EnhanceUseCaseImpl(
    private val repository: ImageEnhancementRepository
) : EnhanceUseCase {
    override suspend fun invoke(image: Image): Image {
        return repository.enhance(image)
    }
}