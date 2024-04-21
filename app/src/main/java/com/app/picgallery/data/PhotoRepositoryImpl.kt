package com.app.picgallery.data

import android.util.Log
import com.app.picgallery.data.network.APIService
import com.app.picgallery.data.network.BaseAPIResponse
import com.app.picgallery.data.network.ResponseState
import com.app.picgallery.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
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


}
