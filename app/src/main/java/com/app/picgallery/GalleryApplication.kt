package com.app.picgallery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
