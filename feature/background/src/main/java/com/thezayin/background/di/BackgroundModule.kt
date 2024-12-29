package com.thezayin.background.di

import com.thezayin.background.data.repository.BackgroundRemovalRepositoryImpl
import com.thezayin.background.domain.repository.BackgroundRemovalRepository
import com.thezayin.background.domain.usecase.RemoveBackgroundUseCase
import com.thezayin.background.domain.usecase.SmoothImageUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val backgroundModule = module {
    single<BackgroundRemovalRepository> {
        BackgroundRemovalRepositoryImpl(
            appContext = androidContext()  // from Koin
        )
    }

    factory {
        RemoveBackgroundUseCase(
            backgroundRemovalRepository = get()
        )
    }

    factory {
        SmoothImageUseCase(
            backgroundRemovalRepository = get()
        )
    }
}
