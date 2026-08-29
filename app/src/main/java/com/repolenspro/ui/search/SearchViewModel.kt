package com.repolenspro.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repolenspro.domain.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search() {
        val currentQuery = _uiState.value.query
        if (currentQuery.isBlank()) return

        viewModelScope.launch {

            //Loading
            _uiState.update { it.copy(isLoading = true, error = null) }

            //Api Call
            val result = repository.searchRepository(currentQuery)

            //Result
            result.onSuccess { repos ->
                _uiState.update { it.copy(repositories = repos, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }
}