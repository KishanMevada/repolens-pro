package com.repolenspro.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.repolenspro.core.database.RepositoryEntity
import com.repolenspro.core.database.GithubDao
import com.repolenspro.core.domain.GithubRepository
import com.repolenspro.core.domain.Repository
import com.repolenspro.core.network.GithubApi
import com.repolenspro.data.paging.GithubRemoteMediator
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
                dao.searchRepositoriesPaged(query)
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
        language = language,
        isBookmarked = isBookmarked
    )
}