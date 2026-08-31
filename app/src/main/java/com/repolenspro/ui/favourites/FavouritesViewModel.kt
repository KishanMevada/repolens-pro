package com.repolenspro.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repolenspro.data.local.dao.GithubDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val dao: GithubDao
) : ViewModel() {
    // ડેટાબેઝમાંથી લાઈવ ફેવરિટ્સ લાવવા માટે
    val favourites = dao.getBookmarkedRepositories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}