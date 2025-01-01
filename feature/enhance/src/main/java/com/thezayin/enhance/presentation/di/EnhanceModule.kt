package com.thezayin.enhance.presentation.di

import com.thezayin.enhance.data.manager.ModelManager
import com.thezayin.enhance.data.manager.ModelManagerImpl
import com.thezayin.enhance.data.repository.ImageEnhancementRepositoryImpl
import com.thezayin.enhance.data.sources.DenoisingDataSource
import com.thezayin.enhance.data.sources.DenoisingDataSourceImpl
import com.thezayin.enhance.data.sources.RealEsrganDataSource
import com.thezayin.enhance.data.sources.RealEsrganDataSourceImpl
import com.thezayin.enhance.data.sources.StyleTransferDataSource
import com.thezayin.enhance.data.sources.StyleTransferDataSourceImpl
import com.thezayin.enhance.domain.repository.ImageEnhancementRepository
import com.thezayin.enhance.domain.usecase.EnhancePlusUseCase
import com.thezayin.enhance.domain.usecase.EnhancePlusUseCaseImpl
import com.thezayin.enhance.domain.usecase.EnhanceProUseCase
import com.thezayin.enhance.domain.usecase.EnhanceProUseCaseImpl
import com.thezayin.enhance.domain.usecase.EnhanceUseCase
import com.thezayin.enhance.domain.usecase.EnhanceUseCaseImpl
import org.koin.dsl.module

val enhanceModule = module {
    single<ModelManager> { ModelManagerImpl(get()) }

    // Provide Data Sources
    single<RealEsrganDataSource> { RealEsrganDataSourceImpl(get()) }
    single<DenoisingDataSource> { DenoisingDataSourceImpl(get()) }
    single<StyleTransferDataSource> { StyleTransferDataSourceImpl(get()) }

    // Provide Repository
    single<ImageEnhancementRepository> { ImageEnhancementRepositoryImpl(get(), get(), get()) }
    single<EnhanceUseCase> { EnhanceUseCaseImpl(get()) }
    single<EnhancePlusUseCase> { EnhancePlusUseCaseImpl(get()) }
    single<EnhanceProUseCase> { EnhanceProUseCaseImpl(get()) }
}