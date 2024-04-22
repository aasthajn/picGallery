package com.app.picgallery.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.picgallery.R
import com.app.picgallery.data.model.Photo
import com.app.picgallery.presentation.HomeViewModel
import com.app.picgallery.presentation.PhotoUIState
import com.app.picgallery.utils.EmptyContent
import com.app.picgallery.utils.ImageState
import com.app.picgallery.utils.LoadingContent
import com.app.picgallery.utils.ShimmerEffect
import com.app.picgallery.utils.SnackbarHost
import com.app.picgallery.utils.rememberContentPaddingForScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: HomeViewModel = hiltViewModel(),
    onNavClick:(String)->Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopAppBar(
                elevation = 4.dp
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = it)
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        PhotoListContent(
            uiState = uiState,
            onRefresh = {
                viewModel.refreshPhotos()
            },
            modifier = modifier.padding(innerPadding)
        ) { hasPhotosUiState, _ ->
            val contentPadding = rememberContentPaddingForScreen(additionalTop = 8.dp)
            val listState = rememberLazyGridState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                state = listState
            ) {
                items(hasPhotosUiState.photoList) { photo ->
                    val imageUrl = photo.getImageUrl()
                    val imageState = loadImageFromUrl(imageUrl, viewModel).value
                    val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavClick(encodedUrl) }
                    ) {
                        when {
                            imageState.bitmap != null -> {
                                Image(
                                    bitmap = imageState.bitmap.asImageBitmap(),
                                    contentDescription = "Loaded image",
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Log.d(
                                    "PicGallery",
                                    "Job Status: ${imageUrl.substring(imageUrl.lastIndex - 15)} : Image displayed"
                                )
                            }

                            imageState.isLoading -> {
                                ShimmerEffect()
                            }

                            imageState.errorMessage != null -> {

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { viewModel.loadImage(imageUrl) {} }.background(Color.LightGray), // Retry on tap
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${imageState.errorMessage} Retry",
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val message = uiState.errorMessage
        if (uiState.errorMessage?.isNotEmpty() == true) {
            LaunchedEffect(message, key2 = scaffoldState, block = {
                val snackbarResult = message?.let {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = "Retry"
                    )
                }
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    viewModel.refreshPhotos()
                }
            })
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
            EmptyContent()
        },
        loading = uiState.isLoading,
        onRefresh = onRefresh,
        content = {
            when (uiState) {
                is PhotoUIState.HasPhotos -> hasPostsContent(uiState, contentModifier)
                is PhotoUIState.NoPhoto -> {
                    uiState.errorMessage?.let {
                        Box(contentModifier.fillMaxSize())
                    }?.run {
                        // if there are no posts, and no error, let the user refresh manually
                        TextButton(
                            onClick = onRefresh,
                            modifier.fillMaxSize()
                        ) {
                            Text(
                                stringResource(id = R.string.pg__home_tap_to_load_content),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    )

}

@Composable
fun loadImageFromUrl(imageUrl: String, viewModel: HomeViewModel): State<ImageState> {
    val state = remember { mutableStateOf(ImageState(isLoading = true)) }

    DisposableEffect(imageUrl) {
        val onResult: (ImageState) -> Unit = { newState ->
            state.value = newState
        }

        viewModel.loadImage(imageUrl, onResult)

        onDispose {
            // Call cancelImageLoad when the composable is disposed
            viewModel.cancelImageLoad(imageUrl)
        }
    }

    return state
}
