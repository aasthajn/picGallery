package com.app.picgallery.data

import com.app.picgallery.data.model.Photo
import com.app.picgallery.data.network.ResponseState
import kotlinx.coroutines.flow.Flow


interface PhotoRepository {

    suspend fun getPhotoList(): Flow<ResponseState<List<Photo>>>
}
