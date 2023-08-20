package com.magstore.lrc.configure

import android.app.Application

object ConfigurationManager {

    lateinit var application: Application
    fun initConfigurations(context: Application) {
        this.application = context
    }
}