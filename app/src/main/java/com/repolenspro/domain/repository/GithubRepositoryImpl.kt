package com.repolenspro.domain.repository

import com.repolenspro.data.model.GithubApi
import com.repolenspro.domain.model.Repository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApi
) : GithubRepository {
    override suspend fun searchRepository(query: String): Result<List<Repository>> {
        return try {
            val response = api.searchRepositories(query)

            val repositories = response.items.map { dto ->
                Repository(
                    id = dto.id,
                    name = dto.name,
                    fullName = dto.fullName,
                    description = dto.description ?: "No description provided",
                    stars = dto.stars,
                    forks = dto.forks,
                    language = dto.language ?: "Unknown"
                )
            }
            Result.success(repositories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


