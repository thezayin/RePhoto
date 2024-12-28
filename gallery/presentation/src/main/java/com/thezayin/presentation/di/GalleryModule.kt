package com.thezayin.presentation.di

import com.thezayin.data.network.MediaStoreUtil
import com.thezayin.data.repository.GetAlbumsRepositoryImpl
import com.thezayin.data.repository.GetImagesRepositoryImpl
import com.thezayin.domain.repository.GetAlbumsRepository
import com.thezayin.domain.repository.GetImagesRepository
import com.thezayin.domain.usecase.AlbumsUseCase
import com.thezayin.domain.usecase.AlbumsUseCaseImpl
import com.thezayin.domain.usecase.ImagesUseCase
import com.thezayin.domain.usecase.ImagesUseCaseImpl
import com.thezayin.presentation.GalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val galleryModule = module {
    single { MediaStoreUtil(get()) }

    single<GetAlbumsRepository> { GetAlbumsRepositoryImpl(get()) }
    single<GetImagesRepository> { GetImagesRepositoryImpl(get()) }
    single<AlbumsUseCase> { AlbumsUseCaseImpl(get()) }
    single<ImagesUseCase> { ImagesUseCaseImpl(get()) }

    viewModelOf(::GalleryViewModel)
}