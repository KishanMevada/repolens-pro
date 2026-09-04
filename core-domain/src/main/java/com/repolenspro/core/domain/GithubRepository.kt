package com.repolenspro.core.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun searchRepository(query: String): Result<List<Repository>>

    fun searchRepositoriesPaged(query: String): Flow<PagingData<Repository>>
}