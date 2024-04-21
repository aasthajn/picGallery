package com.app.picgallery.data.network

sealed class ResponseState<out T> {
    data class Success<out T>(val data: T) : ResponseState<T>()
    data class Error(val error: Exception) : ResponseState<Nothing>()
}
