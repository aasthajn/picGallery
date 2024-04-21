package com.app.picgallery.presentation

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.picgallery.data.PhotoRepository
import com.app.picgallery.data.cache.DiskCache
import com.app.picgallery.data.cache.ImageCache
import com.app.picgallery.data.network.ResponseState.Error
import com.app.picgallery.data.network.ResponseState.Success
import com.app.picgallery.utils.ImageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository,
    private val application: Application
) : AndroidViewModel(application = application) {


    private val viewModelState =
        MutableStateFlow(PhotoViewModelState(isLoading = false, currentPageNo = 1))

    val uiState = viewModelState.map { it.toUIState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUIState())

    init {
        refreshPhotos()
    }

    private val jobMap = mutableMapOf<String, Job>()

    fun loadImage(imageUrl: String, onResult: (ImageState) -> Unit) {
        jobMap[imageUrl]?.cancel() // Cancel any existing job for this URL
        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val memoryCache = ImageCache.getFromMemoryCache(imageUrl)
                if (memoryCache != null) {
                    withContext(Dispatchers.Main) {
                        onResult(ImageState(bitmap = memoryCache, isLoading = false))
                    }
                } else {
                    val diskCache = DiskCache.getBitmapFromDisk(application, imageUrl)
                    if (diskCache != null) {
                        ImageCache.setToMemoryCache(imageUrl, diskCache)
                        withContext(Dispatchers.Main) {
                            onResult(ImageState(bitmap = diskCache, isLoading = false))
                        }
                    } else {
                        val newBitmap = URL(imageUrl).openStream().use {
                            BitmapFactory.decodeStream(it)
                        }
                        newBitmap?.let {
                            ImageCache.setToMemoryCache(imageUrl, it)
                            DiskCache.saveBitmapToDisk(application, imageUrl, it)
                            withContext(Dispatchers.Main) {
                                onResult(ImageState(bitmap = it, isLoading = false))
                            }
                        } ?: run {
                            withContext(Dispatchers.Main) {
                                onResult(ImageState(isError = true))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(ImageState(isError = true))
                }
            }
        }
        jobMap[imageUrl] = job // Store the job
    }

    fun cancelImageLoad(imageUrl: String) {
        jobMap[imageUrl]?.cancel()
        jobMap.remove(imageUrl)
    }
    fun refreshPhotos() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            photoRepository.getPhotoList().map { responseState ->
                when (responseState) {
                    is Success -> viewModelState.update {
                        it.copy(
                            photoList = responseState.data,
                            isLoading = false
                        )
                    }

                    is Error -> viewModelState.update { error ->
                        error.copy(errorMessage = responseState.error.message, isLoading = false)
                    }
                }
            }.collect()
        }
    }


    override fun onCleared() {
        // Cancel all ongoing jobs when the ViewModel is cleared
        jobMap.values.forEach { it.cancel() }
        jobMap.clear()
        super.onCleared()
    }
}
