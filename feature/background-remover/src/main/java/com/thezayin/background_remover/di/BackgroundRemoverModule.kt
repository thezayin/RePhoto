package com.thezayin.background_remover.di

import com.thezayin.background_remover.BackgroundRemoverViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val backgroundRemoverModule = module {
    viewModelOf(::BackgroundRemoverViewModel)
}
