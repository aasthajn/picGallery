package com.app.picgallery.navigation


sealed class NavScreens(val title: String, val route: String) {
    object Home : NavScreens("Home", "home")
    object Detail : NavScreens("detail", "detail")
}

private val screens = listOf(
    NavScreens.Home,
    NavScreens.Detail
)

object GalleryNavArgs{
    const val IMAGE_URL = "imageUrl"
}
