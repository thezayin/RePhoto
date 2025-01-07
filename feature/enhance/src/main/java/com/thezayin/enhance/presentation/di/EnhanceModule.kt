package com.thezayin.enhance.presentation.di

import com.thezayin.enhance.data.repository.EnhanceRepositoryImpl
import com.thezayin.enhance.domain.repository.EnhanceRepository
import com.thezayin.enhance.domain.usecase.EnhanceImageUseCase
import com.thezayin.enhance.presentation.EnhanceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val enhanceModule = module {
    // Provide EnhanceRepository implementation as a singleton
    single<EnhanceRepository> {
        EnhanceRepositoryImpl(context = androidContext())
    }

    // Provide EnhanceImageUseCase as a singleton
    single {
        EnhanceImageUseCase(repository = get())
    }

    viewModelOf(::EnhanceViewModel)
}