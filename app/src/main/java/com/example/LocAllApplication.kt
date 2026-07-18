package com.example

import android.app.Application
import com.example.di.ImageLoaderModule
import com.example.notifications.NotificationHelper

class LocAllApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoaderModule.initialize(this)
        NotificationHelper.createChannels(this)
    }
}
