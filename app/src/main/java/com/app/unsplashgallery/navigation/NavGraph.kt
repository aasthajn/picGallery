package com.app.unsplashgallery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.unsplashgallery.ui.home.HomeScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationActions: NavigationActions,
    onNavClick: () -> Unit,
    startDestination: String = NavScreens.Home.route
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavScreens.Home.route) {
            HomeScreen(onNavClick = onNavClick, modifier)
        }
    }

}
