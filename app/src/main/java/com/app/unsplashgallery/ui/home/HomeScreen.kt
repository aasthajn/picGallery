package com.app.unsplashgallery.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.unsplashgallery.R
import com.app.unsplashgallery.data.model.Photo
import com.app.unsplashgallery.utils.LoadingContent
import com.app.unsplashgallery.utils.SnackbarHost
import com.app.unsplashgallery.utils.loadImageFromUrl
import com.app.unsplashgallery.utils.rememberContentPaddingForScreen

@Composable
fun HomeScreen(
    onNavClick: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopAppBar(
                elevation = 4.dp,
                openDrawer = onNavClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = it)
        }
    ) {innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        PhotoListContent(
            uiState = uiState,
            onRefresh = { viewModel.refreshPhotos() },
            modifier = modifier.padding(innerPadding)
        ) { hasPhotosUiState, contentModifier ->
            val contentPadding = rememberContentPaddingForScreen(additionalTop = 8.dp)

            Row(contentModifier) {
                PhotoList(hasPhotosUiState.photoList)
            }
        }
    }
}

@Composable
fun PhotoList(
    photoList: List<Photo>,
    state: LazyListState = rememberLazyListState()
) {
    val itemModifier = Modifier.border(1.dp, Color.Blue).padding(16.dp).wrapContentSize()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(photoList) { photo ->
            val image = loadImageFromUrl(photo.getImageUrl()).value
            image?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Grid Image",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun Photo.getImageUrl(): String {
    return "${this.thumbnail.domain}/${this.thumbnail.basePath}/0/${this.thumbnail.key}"
}

@Composable
fun PhotoListContent(
    uiState: PhotoUIState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: PhotoUIState.HasPhotos,
        modifier: Modifier
    ) -> Unit
) {
    val contentModifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
    LoadingContent(
        empty = when (uiState) {
            is PhotoUIState.HasPhotos -> false
            is PhotoUIState.NoPhoto -> uiState.isLoading
        },
        emptyContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        },
        loading = uiState.isLoading,
        onRefresh = onRefresh,
        content = {
            when (uiState) {
                is PhotoUIState.HasPhotos -> hasPostsContent(uiState, contentModifier)
                is PhotoUIState.NoPhoto -> {
                    if (uiState.errorMessages.isEmpty()) {
                        // if there are no posts, and no error, let the user refresh manually
                        TextButton(
                            onClick = onRefresh,
                            modifier.fillMaxSize()
                        ) {
                            Text(
                                stringResource(id = R.string.home_tap_to_load_content),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // there's currently an error showing, don't show any content
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        }
    )

}
