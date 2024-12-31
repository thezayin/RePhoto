package com.thezayin.rephoto.application

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.thezayin.background.di.backgroundModule
import com.thezayin.background_changer.di.backgroundChangerModule
import com.thezayin.background_remover.di.backgroundRemoverModule
import com.thezayin.di.analyticsModule
import com.thezayin.framework.di.featureModule
import com.thezayin.framework.notification.NotificationUtil
import com.thezayin.presentation.di.galleryModule
import com.thezayin.start_up.di.splashModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createNotificationChannel(this)
        MobileAds.initialize(this)
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                featureModule,
                analyticsModule,
                galleryModule,
                splashModule,
                backgroundModule,
                backgroundRemoverModule,
                backgroundChangerModule
            )
        }
    }
}