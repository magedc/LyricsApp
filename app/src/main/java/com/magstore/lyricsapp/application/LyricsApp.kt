package com.magstore.lyricsapp.application

import android.app.Application
import com.magstore.lrc.configure.ConfigurationManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LyricsApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ConfigurationManager.initConfigurations(this)
        startKoin() {
            androidLogger()
            androidContext(this@LyricsApp)
            modules(viewModelModules, helperModels)


        }
    }
}