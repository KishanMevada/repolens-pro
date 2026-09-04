package com.repolenspro.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.repolenspro.core.domain.Repository
import com.repolenspro.core.domain.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    // 1. માત્ર ટાઈપિંગ બતાવવા માટે
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. જ્યારે યુઝર સર્ચ બટન દબાવે ત્યારે ફાઈનલ શબ્દ અહીં આવશે
    private val _submittedQuery = MutableStateFlow("")
    val submittedQuery = _submittedQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedRepositories: Flow<PagingData<Repository>> = _submittedQuery
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            repository.searchRepositoriesPaged(query)
        }
        .cachedIn(viewModelScope)

    // ટાઈપિંગ થાય ત્યારે આ કોલ થશે
    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // બટન દબાવે ત્યારે આ કોલ થશે
    fun onSearchClicked() {
        _submittedQuery.value = _searchQuery.value
    }
}