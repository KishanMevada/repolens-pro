package com.repolenspro.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import com.repolenspro.domain.model.Repository

class GithubPagingSource(
    private val api: GithubApi,
    private val dao: GithubDao,
    private val query: String
): PagingSource<Int, Repository>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.searchRepositories(query = query, page = 1, perPage = 25)

            // 1. API ના ડેટાને Database (Entity) ફોર્મેટમાં ફેરવીને રૂમમાં સેવ કરો
            val entities = response.items.map { dto ->
                RepositoryEntity(
                    id = dto.id,
                    name = dto.name,
                    fullName = dto.fullName,
                    description = dto.description ?: "No description provided",
                    stars = dto.stars,
                    forks = dto.forks,
                    language = dto.language ?: "Unknown",
                    false,
                    ""
                )
            }
            dao.insertRepositories(entities)

            val repositories = entities.map {
                Repository(
                    id = it.id,
                    name = it.name,
                    fullName = it.fullName,
                    description = it.description,
                    stars = it.stars,
                    forks = it.forks,
                    language = it.language,
                    false
                )
            }

            LoadResult.Page(
                data = repositories,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (repositories.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
        return state.anchorPosition
    }
}