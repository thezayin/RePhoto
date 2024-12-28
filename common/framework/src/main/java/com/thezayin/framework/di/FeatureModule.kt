package com.thezayin.framework.di

import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.framework.session.SessionManager
import com.thezayin.framework.session.SessionManagerImpl
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val featureModule = module {
    single<SessionManager> { SessionManagerImpl() }
    single { Json { ignoreUnknownKeys = true } }
    single { RemoteConfig(get()) }
}