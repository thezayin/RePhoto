package com.thezayin.background.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thezayin.background.data.repository.BackgroundRemovalRepositoryImpl
import com.thezayin.background.domain.repository.BackgroundRemovalRepository
import com.thezayin.background.domain.repository.ImageRepository
import com.thezayin.background.domain.usecase.GetBackgroundImagesUseCase
import com.thezayin.background.domain.usecase.MergeImagesUseCase
import com.thezayin.background.domain.usecase.RemoveBackgroundUseCase
import com.thezayin.background.domain.usecase.SaveImageUseCase
import com.thezayin.background.domain.usecase.SmoothImageUseCase
import com.thezayin.background.data.repository.ImageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val backgroundModule = module {

    // Provide Gson instance
    single<Gson> {
        GsonBuilder()
            .setLenient()
            .create()
    }

    // Provide Context
    single<Context> { get<android.app.Application>() }

    // Provide ImageRepository
    single<ImageRepository> { ImageRepositoryImpl(get()) }

    // Provide Use Cases
    single { MergeImagesUseCase(get()) }
    single { SaveImageUseCase(get()) }

    // Provide FirebaseRemoteConfig instance
    single<FirebaseRemoteConfig> {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(com.thezayin.values.R.xml.remote_config_defaults)
        remoteConfig
    }

    // Provide BackgroundRemovalRepository
    single<BackgroundRemovalRepository> {
        BackgroundRemovalRepositoryImpl(
            appContext = androidContext(),
            remoteConfig = get(),
            gson = get()
        )
    }

    // Provide Use Cases
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

    factory {
        GetBackgroundImagesUseCase(
            repository = get()
        )
    }
}
