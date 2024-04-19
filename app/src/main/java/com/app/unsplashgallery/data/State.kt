package com.app.unsplashgallery.data

sealed class ResponseState<out T> {
    data class Success<out T>(val data: T) : ResponseState<T>()
    data class Error(val errorMessages: List<Exception>) : ResponseState<Nothing>()
}
