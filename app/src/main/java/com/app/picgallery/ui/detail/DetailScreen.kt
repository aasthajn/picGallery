package com.app.picgallery.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.picgallery.R
import com.app.picgallery.presentation.HomeViewModel
import com.app.picgallery.ui.home.loadImageFromUrl

@Composable
fun DetailScreen(
    imageUrl: String,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavClick: () -> Unit
) {
    Column {
        DetailsTopAppBar(4.dp, onNavClick)
        val imageState = loadImageFromUrl(imageUrl, viewModel).value
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                imageState.bitmap != null -> Image(
                    bitmap = imageState.bitmap.asImageBitmap(),
                    contentDescription = "Detailed Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                else -> CircularProgressIndicator()  // Show loading indicator
            }
        }
    }
}

@Composable
fun DetailsTopAppBar(
    elevation: Dp,
    onNavClick: () -> Unit
) {
    val title = stringResource(id = R.string.pg__details)
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onNavClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.pg__details_desc),
                    tint = MaterialTheme.colors.primary
                )
            }

        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = elevation,
        modifier = Modifier.fillMaxWidth()
    )
}
