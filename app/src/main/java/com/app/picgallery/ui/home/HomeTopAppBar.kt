package com.app.picgallery.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.app.com.app.picgallery.R

@Composable
fun HomeTopAppBar(
    elevation: Dp
) {
    val title = stringResource(id = R.string.pg__app_name)
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = stringResource(R.string.pg__home),
                tint = MaterialTheme.colors.primary
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = elevation,
        modifier = Modifier.fillMaxWidth()
    )
}
