package com.app.picgallery.data.network

import com.app.picgallery.data.model.Photo
import retrofit2.http.GET

interface APIService {
    @GET("misc/media-coverages?limit=100")
    suspend fun getPhotos(): List<Photo>

}
