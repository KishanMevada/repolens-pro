package com.repolenspro.ui.search

import com.repolenspro.domain.model.Repository

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val error: String? = null
)