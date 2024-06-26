package com.app.picgallery.presentation

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.picgallery.Constants
import com.app.picgallery.Constants.Companion.LOG_TAG
import com.app.picgallery.data.PhotoRepository
import com.app.picgallery.data.cache.DiskCache
import com.app.picgallery.data.cache.ImageCache
import com.app.picgallery.data.network.ResponseState.Error
import com.app.picgallery.data.network.ResponseState.Success
import com.app.picgallery.utils.ImageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
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

    val uiState = viewModelState.map {
        it.toUIState()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUIState())

    init {
        refreshPhotos()
    }

    //map of imageUrls and corresponding image download map
    private val jobMap = mutableMapOf<String, Job>()

    //imageState is returned after getting image from memcache, diskcache or downloading from internet
    fun loadImage(imageUrl: String, onResult: (ImageState) -> Unit) {
        val imagePath = imageUrl.substring(imageUrl.lastIndex - 15)
        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isActive) {
                    Log.d(
                        LOG_TAG,
                        "Job Status: $imagePath:  Started"
                    )
                    val memoryCache = ImageCache.getFromMemoryCache(imageUrl)
                    if (memoryCache != null) {
                        Log.d(
                            LOG_TAG,
                            "Job Status: $imagePath : Memcache success"
                        )
                        withContext(Dispatchers.Main) {
                            onResult(ImageState(bitmap = memoryCache, isLoading = false))
                        }
                    } else {
                        val diskCache = DiskCache.getBitmapFromDisk(application, imageUrl)
                        if (diskCache != null) {
                            Log.d(
                                LOG_TAG,
                                "Job Status: $imagePath : Disk Cache success"
                            )
                            ImageCache.setToMemoryCache(imageUrl, diskCache)
                            withContext(Dispatchers.Main) {
                                onResult(ImageState(bitmap = diskCache, isLoading = false))
                            }
                        } else {
                            Log.d(
                                LOG_TAG,
                                "Job Status: $imagePath : Download started"
                            )

                            delay(Constants.IMAGE_LOAD_DELAY)
                            if (isActive) {
                                yield()
                                try {
                                    val newBitmap = URL(imageUrl).openStream().use {
                                        BitmapFactory.decodeStream(it)
                                    }
                                    newBitmap?.let {
                                        Log.d(
                                            LOG_TAG,
                                            "Job Status: $imagePath : Download success"
                                        )
                                        ImageCache.setToMemoryCache(imageUrl, it)
                                        DiskCache.saveBitmapToDisk(application, imageUrl, it)
                                        withContext(Dispatchers.Main) {
                                            onResult(ImageState(bitmap = it, isLoading = false))
                                        }
                                    } ?: run {
                                        withContext(Dispatchers.Main) {
                                            Log.d(
                                                LOG_TAG,
                                                "Job Status: $imagePath : Image load error"
                                            )

                                            onResult(
                                                ImageState(
                                                    isLoading = false,
                                                    errorMessage = "Error downloading image."
                                                )
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Handle exceptions related to network or decoding failures
                                    Log.e(
                                        LOG_TAG,
                                        "Job Status: $imagePath : Load exception", e
                                    )

                                    withContext(Dispatchers.Main) {
                                        onResult(
                                            ImageState(
                                                isLoading = false,
                                                errorMessage = "Error downloading image."
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (cancellation: CancellationException) {
                // When we cancel the job in onDispose, this gets called
                Log.d(
                    "PicGallery",
                    "Job status:  ${imageUrl.substring(imageUrl.lastIndex - 15)} : Cancelled"
                )
            } catch (e: Exception) {
                Log.d(
                    "PicGallery",
                    "Job status: ${imageUrl.substring(imageUrl.lastIndex - 15)} : Exception"
                )
                withContext(Dispatchers.Main) {
                    onResult(
                        ImageState(
                            errorMessage = e.message ?: "Something went wrong "

                        )
                    )
                }
            }
        }
        jobMap[imageUrl] = job // Store the job
    }

    //Cancel Image Download for Job with this imageUrl
    fun cancelImageLoad(imageUrl: String) {
        viewModelScope.launch {
            Log.d(
                "PicGallery",
                "Job status:  ${imageUrl.substring(imageUrl.lastIndex - 15)} : Cancel Initiated"
            )
            jobMap[imageUrl]?.cancelAndJoin()
            jobMap.remove(imageUrl)
        }
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
