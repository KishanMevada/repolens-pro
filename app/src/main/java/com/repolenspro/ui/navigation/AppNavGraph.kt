package com.repolenspro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.repolenspro.ui.detail.RepositoryDetailScreen
import com.repolenspro.ui.search.SearchScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                onNavigationToDetail = {
                    navController.navigate("detail")
                }
            )
        }

        composable("detail") {
            RepositoryDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}