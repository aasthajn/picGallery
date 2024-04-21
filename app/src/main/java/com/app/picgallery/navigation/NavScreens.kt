package com.app.picgallery.navigation


sealed class NavScreens(val title: String, val route: String) {
    object Home : NavScreens("Home", "home")
}

private val screens = listOf(
    NavScreens.Home
)
