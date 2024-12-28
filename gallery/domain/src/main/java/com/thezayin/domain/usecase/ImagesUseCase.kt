package com.thezayin.domain.usecase

import com.thezayin.domain.model.Image
import com.thezayin.domain.repository.GetImagesRepository
import com.thezayin.framework.utils.Response
import kotlinx.coroutines.flow.Flow

interface ImagesUseCase {
    suspend operator fun invoke(albumId: String): Flow<Response<List<Image>>>
}

class ImagesUseCaseImpl(private val repository: GetImagesRepository) : ImagesUseCase {
    override suspend fun invoke(albumId: String): Flow<Response<List<Image>>> =
        repository.getImages(albumId)
}
