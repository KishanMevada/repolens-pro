package com.repolenspro.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.repolenspro.domain.model.Repository
import com.repolenspro.ui.components.ErrorStateItem
import com.repolenspro.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToFavourites: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val submittedQuery by viewModel.submittedQuery.collectAsState()
    val repositories = viewModel.pagedRepositories.collectAsLazyPagingItems()

    // લિસ્ટનું સ્ક્રોલ કંટ્રોલ કરવા માટે
    val listState = rememberLazyListState()

    // જ્યારે નવો ડેટા લોડ થતો હોય ત્યારે લિસ્ટને ઓટોમેટિક સૌથી ઉપર (Top) મોકલી દો
    LaunchedEffect(repositories.loadState.refresh) {
        if (repositories.loadState.refresh is LoadState.Loading) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RepoLens Pro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                actions = {
                    val isDark by themeViewModel.isDarkMode.collectAsState()
                    // ✅ નવું ફેવરિટ્સ બટન ઉમેર્યું (થીમ બટનની બાજુમાં)
                    IconButton(onClick = onNavigateToFavourites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favourites",
                            tint = Color.Red
                        )
                    }
                    IconButton(onClick = { themeViewModel.toggleTheme(!isDark) }) {
                        Icon(
                            // લાઈટ/ડાર્ક મોડ મુજબ આઇકન બદલાશે
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = query,
                onQueryChange = viewModel::onQueryChanged,
                onSearch = {
                    viewModel.onSearchClicked()
                    repositories.retry() // જૂની એરર ભૂંસીને ફરજિયાત નવો કોલ કરશે
                },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ✅ ફિક્સ: ટાઇપ કરેલી નહિ, પણ 'સબમિટ' કરેલી ક્વેરી ખાલી હોય તો જ મેસેજ બતાવો
                if (submittedQuery.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Enter a keyword to search repositories \uD83D\uDD0D",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    // ✅ ૧. જો પહેલી જ વાર ડેટા લાવવામાં ભૂલ પડે (Initial Load Error)
                    if (repositories.loadState.refresh is LoadState.Error) {
                        val error = (repositories.loadState.refresh as LoadState.Error).error
                        item {
                            ErrorStateItem(
                                modifier = Modifier.fillParentMaxSize(),
                                message = error.localizedMessage ?: "No Internet Connection.",
                                onRetry = { repositories.retry() } // Paging 3 નું જાદુઈ retry ફંક્શન
                            )
                        }
                    }

                    // ૧. લિસ્ટનો ડેટા બતાવવા માટે
                    items(
                        count = repositories.itemCount,
                        key = { index ->
                            repositories.peek(index)?.id ?: index
                        }
                    ) { index ->
                        val repo = repositories[index]
                        if (repo != null) {
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
                                        val encodedName =
                                            java.net.URLEncoder.encode(repo.fullName, "UTF-8")
                                        onNavigateToDetail(encodedName)
                                    }
                                )
                            }
                        }
                    }

                    // ✅ ૩. જો નીચે સ્ક્રોલ કરતી વખતે નવો ડેટા લાવવામાં ભૂલ પડે (Pagination Error)
                    if (repositories.loadState.append is LoadState.Error) {
                        val error = (repositories.loadState.append as LoadState.Error).error
                        item {
                            ErrorStateItem(
                                message = error.localizedMessage ?: "Could not load more.",
                                onRetry = { repositories.retry() }
                            )
                        }
                    }

                    // ૨. એરર મેસેજ (API લિમિટ માટે) બતાવવા માટે
                    val mediatorState = repositories.loadState.mediator?.refresh
                    if (mediatorState is LoadState.Error) {
                        item {
                            Text(
                                text = "API Limit Reached! 1 મિનિટ રાહ જુઓ અને પછી ફરી સર્ચ કરો.",
                                color = Color.Red,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }

                    // ૩. લોડિંગ ઇન્ડિકેટર (Spinner) બતાવવા માટે
                    if (repositories.loadState.append is LoadState.Loading || repositories.loadState.refresh is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search Github repositories...") },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search // કીબોર્ડમાં સર્ચનું આઇકન લાવવા માટે
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() } // કીબોર્ડનું આઇકન દબાવવાથી એક્શન થશે
        )
    )
}

@Composable
fun RepositoryItem(repository: Repository, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = repository.fullName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = repository.description.ifBlank { "No description available." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 🌟 Premium Footer with Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconText(
                    icon = Icons.Default.Star,
                    text = repository.stars.toString(),
                    tint = Color(0xFFFFC107)
                )
                IconText(
                    icon = Icons.Default.CallSplit,
                    text = repository.forks.toString(),
                    tint = MaterialTheme.colorScheme.secondary
                )
                IconText(
                    icon = Icons.Default.Code,
                    text = repository.language,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun IconText(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
