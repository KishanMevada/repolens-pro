package com.repolenspro.domain.repository

import com.repolenspro.domain.model.Repository
import retrofit2.http.Query

interface GithubRepository {
    suspend fun searchRepository(query: String): Result<List<Repository>>
}