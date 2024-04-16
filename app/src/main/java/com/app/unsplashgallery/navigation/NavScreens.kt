package com.app.unsplashgallery.navigation


sealed class NavScreens(val title: String, val route: String) {
    object Home : NavScreens("Home", "home")
    object Favorite : NavScreens("Favorite", "favorite")
}

private val screens = listOf(
    NavScreens.Home,
    NavScreens.Favorite
)
