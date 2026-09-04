package com.repolenspro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.repolenspro.feature.detail.RepositoryDetailScreen
import com.repolenspro.feature.favourites.FavouritesScreen
import com.repolenspro.feature.search.SearchScreen
import com.repolenspro.ui.theme.ThemeViewModel
import java.net.URLEncoder

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = "search") {
        composable("search") {

            val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)

            SearchScreen(
                isDarkMode = isDarkMode,
                onThemeToggle = { themeViewModel.toggleTheme(!isDarkMode) },
                onNavigateToDetail = { repoName ->
                    if (repoName.isNotBlank()) {
                        val encodedName = URLEncoder.encode(repoName, "UTF-8")
                        navController.navigate("detail/$encodedName")
                    }
                },
                onNavigateToFavourites = {
                    navController.navigate("favourites")
                }

            )
        }

        composable(
            route = "detail/{repoName}",
            arguments = listOf(navArgument("repoName") { type = NavType.StringType })
        ) { backStackEntry ->

            val repoName = backStackEntry.arguments?.getString("repoName") ?: "Unknown"
            val decodedRepoName = java.net.URLDecoder.decode(repoName, "UTF-8")

            RepositoryDetailScreen(
                repoName = decodedRepoName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ✅ નવો કમ્પોઝેબલ રૂટ ઉમેરો
        composable("favourites") {
            FavouritesScreen(
                onNavigateToDetail = { repoName ->
                    if (repoName.isNotBlank()) {
                        val encodedName = URLEncoder.encode(repoName, "UTF-8")
                        navController.navigate("detail/$encodedName")
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}