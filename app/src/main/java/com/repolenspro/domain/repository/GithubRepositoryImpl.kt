package com.repolenspro.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import com.repolenspro.data.paging.GithubRemoteMediator
import com.repolenspro.domain.model.Repository
import com.repolenspro.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApi,
    private val dao: GithubDao
) : GithubRepository {
    override suspend fun searchRepository(query: String): Result<List<Repository>> {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun searchRepositoriesPaged(query: String): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                prefetchDistance = 2,
                initialLoadSize = 25,
                enablePlaceholders = false
            ),
            remoteMediator = GithubRemoteMediator(
                query = query,
                api = api,
                dao = dao
            ),
            pagingSourceFactory = {
                dao.searchRepositoriesPaged()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomainModel()
            }
        }
    }
}

// Extension Mapper Function
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