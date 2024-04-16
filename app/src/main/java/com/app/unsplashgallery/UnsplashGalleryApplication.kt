package com.app.unsplashgallery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UnsplashGalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
