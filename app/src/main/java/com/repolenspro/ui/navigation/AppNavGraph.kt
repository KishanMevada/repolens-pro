package com.repolenspro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.repolenspro.ui.detail.RepositoryDetailScreen
import com.repolenspro.ui.search.SearchScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                onNavigateToDetail = { repoName ->
                    if (repoName.isNotBlank()) {
                        val encodedName = java.net.URLEncoder.encode(repoName, "UTF-8")
                        navController.navigate("detail/$encodedName")
                    }
                }
            )
        }

        composable(
            route = "detail/{repoName}",
            arguments = listOf(navArgument("repoName") { type = NavType.StringType })
        ) { backStackEntry ->

            val repoName = backStackEntry.arguments?.getString("repoName") ?: "Unknown"

            RepositoryDetailScreen(
                repoName = repoName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}