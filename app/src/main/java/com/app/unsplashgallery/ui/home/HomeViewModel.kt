package com.app.unsplashgallery.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.unsplashgallery.data.PhotoRepository
import com.app.unsplashgallery.data.ResponseState.Error
import com.app.unsplashgallery.data.ResponseState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(PhotoViewModelState(isLoading = false, currentPageNo = 1))

    val uiState = viewModelState.map { it.toUIState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUIState())

    init {
        refreshPhotos()
    }

    fun refreshPhotos() {
        //viewModelState.update { it.copy(isLoading = true) }
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
                        error.copy(errorMessages = responseState.errorMessages.map {
                            it.message ?: ""
                        }, isLoading = false)
                    }
                }
            }.collect()
        }
    }


}
