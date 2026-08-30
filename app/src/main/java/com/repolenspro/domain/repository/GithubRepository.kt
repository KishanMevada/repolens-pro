package com.repolenspro.domain.repository

import androidx.paging.PagingData
import com.repolenspro.domain.model.Repository
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun searchRepository(query: String): Result<List<Repository>>

    fun searchRepositoriesPaged(query: String): Flow<PagingData<Repository>>
}