package com.repolenspro.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repolenspro.core.database.GithubDao
import com.repolenspro.core.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val dao: GithubDao
) : ViewModel() {

    private val _repository = MutableStateFlow<Repository?>(null)
    val repository: StateFlow<Repository?> = _repository.asStateFlow()

    fun fetchRepositoryDetails(repoName: String) {
        viewModelScope.launch {
            val entity = dao.getRepositoryByName(repoName)
            if (entity != null) {
                _repository.value = Repository(
                    id = entity.id,
                    name = entity.name,
                    fullName = entity.fullName,
                    description = entity.description,
                    stars = entity.stars,
                    forks = entity.forks,
                    language = entity.language,
                    isBookmarked = entity.isBookmarked
                )
            }
        }
    }

    fun toggleBookmark(repoId: Int, isCurrentlyBookmarked: Boolean, repoName: String) {
        viewModelScope.launch {
            dao.updateBookmarkStatus(repoId, !isCurrentlyBookmarked)
            fetchRepositoryDetails(repoName)
        }
    }
}