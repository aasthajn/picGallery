package com.app.unsplashgallery.data

import android.util.Log
import com.app.unsplashgallery.Constants
import com.app.unsplashgallery.data.model.Photo
import com.app.unsplashgallery.data.network.APIService
import com.app.unsplashgallery.di.ApplicationScope
import com.app.unsplashgallery.di.DataModules
import com.app.unsplashgallery.di.DefaultDispatcher
import com.app.unsplashgallery.di.GalleryModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : PhotoRepository,
    BaseAPIResponse() {

    override suspend fun getPhotoList() = flow {
            try {
                val result = apiService.getPhotos()
                Log.d("Aastha success",result.toString())
                emit(ResponseState.Success(result))
            } catch (exception: Exception) {
                Log.d("Aastha error",exception.toString())
                emit(resolveError(exception))
            }
    }.flowOn(dispatcher)

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override fun getImageFromThumbnail(photo: Photo) {
        val url =
            "${photo.thumbnail.domain}$/${photo.thumbnail.basePath}$\"/0/\"${photo.thumbnail.key}"
    }

}
