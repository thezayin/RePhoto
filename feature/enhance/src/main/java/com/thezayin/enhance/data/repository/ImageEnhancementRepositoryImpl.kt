package com.thezayin.enhance.data.repository

import com.thezayin.enhance.data.sources.DenoisingDataSource
import com.thezayin.enhance.data.sources.RealEsrganDataSource
import com.thezayin.enhance.data.sources.StyleTransferDataSource
import com.thezayin.enhance.domain.model.Image
import com.thezayin.enhance.domain.repository.ImageEnhancementRepository

class ImageEnhancementRepositoryImpl(
    private val realEsrganDataSource: RealEsrganDataSource,
    private val denoisingDataSource: DenoisingDataSource,
    private val styleTransferDataSource: StyleTransferDataSource
) : ImageEnhancementRepository {

    override suspend fun enhance(image: Image): Image {
        return realEsrganDataSource.enhanceImage(image)
    }

    override suspend fun enhancePlus(image: Image): Image {
        val enhancedImage = realEsrganDataSource.enhanceImage(image)
        return denoisingDataSource.denoiseImage(enhancedImage)
    }

    override suspend fun enhancePro(image: Image): Image {
        var processedImage = realEsrganDataSource.enhanceImage(image)
        processedImage = denoisingDataSource.denoiseImage(processedImage)
        return styleTransferDataSource.applyStyle(processedImage)
    }
}