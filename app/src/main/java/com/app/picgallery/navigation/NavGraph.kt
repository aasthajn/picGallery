package com.app.picgallery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavArgs
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.picgallery.ui.detail.DetailScreen
import com.app.picgallery.ui.home.HomeScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavScreens.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavScreens.Home.route) {
            HomeScreen(modifier) {
                navController.run { navigate("${NavScreens.Detail.route}/${it}") }
            }
        }
        composable(
            "${NavScreens.Detail.route}/{imageUrl}",
            arguments = listOf(navArgument(GalleryNavArgs.IMAGE_URL) { type = NavType.StringType })
        ) { backStackEntry ->
            DetailScreen(imageUrl = backStackEntry.arguments?.getString(GalleryNavArgs.IMAGE_URL) ?: "") {
                navController.popBackStack()
            }
        }

    }

}
