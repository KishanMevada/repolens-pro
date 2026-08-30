package com.repolenspro.data.paging
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val api: GithubApi,
    private val dao: GithubDao
) : RemoteMediator<Int, RepositoryEntity>() {

    private var pageIndex = 1
    private val mutex = Mutex()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepositoryEntity>
    ): MediatorResult {
        return mutex.withLock {
            try {
                val currentPage = when (loadType) {
                    LoadType.REFRESH -> {
                        pageIndex = 1
                        1
                    }
                    LoadType.PREPEND -> {
                        return@withLock MediatorResult.Success(endOfPaginationReached = true)
                    }
                    LoadType.APPEND -> {
                        // સળંગ કે ડબલ કૉલ રોકવા માટે સીધો જ પેજ ઇન્ડેક્સ ૧ પોઈન્ટ આગળ વધારવો
                        pageIndex++
                        pageIndex
                    }
                }

                // API કૉલ (perPage = 25 સાથે)
                val response = api.searchRepositories(query = query, page = currentPage, perPage = 25)
                val entities = response.items.map { dto ->
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

                // જો નવું સર્ચ હોય તો જ ડેટાબેઝ સાફ કરવો
                if (loadType == LoadType.REFRESH) {
                    dao.clearAllRepositories()
                }

                // DB માં ડેટા સેવ કરવો
                dao.insertRepositories(entities)

                // જો ડેટા ખાલી હોય અથવા ૨૫ કરતા ઓછા હોય તો પૅજિંગ અટકાવવું
                val endOfPaginationReached = entities.isEmpty() || entities.size < 25
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

            } catch (e: IOException) {
                MediatorResult.Error(e)
            } catch (e: HttpException) {
                MediatorResult.Error(e)
            }
        }
    }
}