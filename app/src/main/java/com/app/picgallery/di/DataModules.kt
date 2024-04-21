package com.app.picgallery.di

import com.app.picgallery.data.PhotoRepository
import com.app.picgallery.data.PhotoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class DataModules {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Singleton
        @Binds
        abstract fun bindPhotoRepository(repository: PhotoRepositoryImpl): PhotoRepository
    }
}
