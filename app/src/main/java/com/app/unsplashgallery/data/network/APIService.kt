package com.app.unsplashgallery.data.network

import com.app.unsplashgallery.data.model.Photo
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("misc/media-coverages?limit=100")
    suspend fun getPhotos(): List<Photo>

}
