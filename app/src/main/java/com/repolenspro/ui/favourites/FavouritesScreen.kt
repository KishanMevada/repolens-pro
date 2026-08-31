package com.repolenspro.ui.favourites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.repolenspro.domain.model.Repository
import com.repolenspro.ui.search.RepositoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: FavouritesViewModel = hiltViewModel()
) {

    val favourites by viewModel.favourites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorites ❤️") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        if (favourites.isEmpty()) {
            // જો કશું જ સેવ ના કર્યું હોય તો આ મેસેજ દેખાશે
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorites yet.\nGo search and add some! \uD83D\uDE04",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = favourites.size,
                    key = { index -> favourites[index].id }
                ) { index ->
                    val entity = favourites[index]

                    // Entity ને Domain Model (Repository) માં કન્વર્ટ કર્યું
                    val repo = Repository(
                        id = entity.id,
                        name = entity.name,
                        fullName = entity.fullName,
                        description = entity.description,
                        stars = entity.stars,
                        forks = entity.forks,
                        language = entity.language,
                        isBookmarked = entity.isBookmarked
                    )

                    AnimatedVisibility(
                        visible = true, // જ્યારે પણ આઇટમ સ્ક્રીન પર આવે
                        enter = fadeIn(animationSpec = tween(durationMillis = 400)) +
                                slideInVertically(
                                    animationSpec = tween(durationMillis = 400),
                                    initialOffsetY = { it / 2 })
                    ) {
                        RepositoryItem(
                            repository = repo,
                            onClick = {
                                val encodedName = java.net.URLEncoder.encode(repo.fullName, "UTF-8")
                                onNavigateToDetail(encodedName)
                            }
                        )
                    }
                }
            }
        }
    }

}