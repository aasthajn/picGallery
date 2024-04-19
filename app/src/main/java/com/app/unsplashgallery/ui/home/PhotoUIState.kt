package com.app.unsplashgallery.ui.home

import com.app.unsplashgallery.data.model.Photo

data class PhotoViewModelState(
    val photoList: List<Photo>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val currentPageNo: Int
) {
    fun toUIState(): PhotoUIState =
        if (photoList == null) {
            PhotoUIState.NoPhoto(
                isLoading = isLoading,
                errorMessages = emptyList(),
            )
        } else {
            PhotoUIState.HasPhotos(
                photoList = photoList,
                currentPageNo = currentPageNo, isLoading = isLoading,
                errorMessages = errorMessages
            )
        }
}

sealed interface PhotoUIState {
    val isLoading: Boolean
    val errorMessages: List<String>

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoPhoto(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PhotoUIState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasPhotos(
        val photoList: List<Photo>,
        val currentPageNo: Int,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PhotoUIState
}
