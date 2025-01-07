package com.thezayin.background_blur.di

import com.thezayin.background_blur.BackgroundBlurViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val blurModule = module {
    viewModelOf(::BackgroundBlurViewModel)
}