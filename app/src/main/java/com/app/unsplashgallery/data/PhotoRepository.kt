package com.app.unsplashgallery.data

import com.app.unsplashgallery.data.model.Photo
import kotlinx.coroutines.flow.Flow


interface PhotoRepository {

    suspend fun getPhotoList() : Flow<ResponseState<List<Photo>>>

    suspend fun refresh()

    fun getImageFromThumbnail(photo: Photo)
}
