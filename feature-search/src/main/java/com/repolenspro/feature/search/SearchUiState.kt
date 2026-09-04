package com.repolenspro.feature.search

import com.repolenspro.core.domain.Repository

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val error: String? = null
)