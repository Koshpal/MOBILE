package com.app.koshpal

import android.app.Application
import com.app.koshpal.app.di.appModule
import com.app.koshpal.core.data.networking.BaseUrl
import com.app.koshpal.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class KoshpalApp: Application() {
    override fun onCreate() {
        super.onCreate()
        BaseUrl.value = BuildConfig.BASE_URL
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@KoshpalApp)
            androidLogger()
            modules(sharedModule, appModule)
        }
    }
}
