package com.lartschy.beyond90

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FootballApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .logger(DebugLogger())
                .build()
        )
    }
}