package com.repolenspro.domain.repository

import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import com.repolenspro.domain.model.Repository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApi,
    private val dao: GithubDao
) : GithubRepository {
    override suspend fun searchRepository(query: String): Result<List<Repository>> {
        return try {
            val response = api.searchRepositories(query)

            val entity = response.items.map { dto ->
                RepositoryEntity(
                    id = dto.id,
                    name = dto.name,
                    fullName = dto.fullName,
                    description = dto.description ?: "No description provided",
                    stars = dto.stars,
                    forks = dto.forks,
                    language = dto.language ?: "Unknown"
                )
            }

            //Saving Data
            dao.insertRepositories(entity)

            //Getting Data
            val domainModels = dao.searchRepositories(query).map { it.toDomainModel() }

            Result.success(domainModels)

        } catch (e: Exception) {

            val cachedData = dao.searchRepositories(query)
            if (cachedData.isNotEmpty()) {
                val domainModels = cachedData.map { it.toDomainModel() }
                return Result.success(domainModels)
            } else {
                return Result.failure(e)
            }
        }
    }

    private fun RepositoryEntity.toDomainModel(): Repository {
        return Repository(
            id = id,
            name = name,
            fullName = fullName,
            description = description,
            stars = stars,
            forks = forks,
            language = language
        )
    }
}


