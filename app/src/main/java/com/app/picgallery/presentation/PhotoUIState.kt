package com.app.picgallery.presentation

import com.app.picgallery.data.model.Photo

data class PhotoViewModelState(
    val photoList: List<Photo>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String ?=null,
    val currentPageNo: Int
) {
    fun toUIState(): PhotoUIState =
        if (photoList == null) {
            PhotoUIState.NoPhoto(
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        } else {
            PhotoUIState.HasPhotos(
                photoList = photoList,
                currentPageNo = currentPageNo, isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
}

sealed interface PhotoUIState {
    val isLoading: Boolean
    val errorMessage: String?

    /**
     * There are no photos to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoPhoto(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : PhotoUIState

    /**
     * There are photos to render, as contained in [postsFeed].
     *
     */
    data class HasPhotos(
        val photoList: List<Photo>,
        val currentPageNo: Int,
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : PhotoUIState
}
