package com.thezayin.enhance.domain.repository

import com.thezayin.enhance.domain.model.Image

interface ImageEnhancementRepository {
    suspend fun enhance(image: Image): Image
    suspend fun enhancePlus(image: Image): Image
    suspend fun enhancePro(image: Image): Image
}