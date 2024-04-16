package com.app.unsplashgallery.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.unsplashgallery.R
import com.app.unsplashgallery.utils.SnackbarHost

@Composable
fun HomeScreen(
    onNavClick: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(scaffoldState = scaffoldState,
        modifier = modifier,
        topBar = {
            HomeTopAppBar(
                elevation = 4.dp,
                openDrawer = onNavClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = it)
        }) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        LazyColumn(
            modifier = modifier,
            contentPadding = innerPadding,
            state = rememberLazyListState()
        ) {
            item { PostListTopSection() }
        }
    }
}

@Composable
fun PostListTopSection() {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.ug__home_top_section_title),
        style = MaterialTheme.typography.subtitle1
    )
}
