package com.thezayin.background_changer.di

import com.thezayin.background_changer.BackgroundChangerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val backgroundChangerModule = module {
    viewModelOf(::BackgroundChangerViewModel)
}