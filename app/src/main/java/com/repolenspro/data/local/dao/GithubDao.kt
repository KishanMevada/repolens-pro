package com.repolenspro.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.repolenspro.data.local.RepositoryEntity

@Dao
@JvmSuppressWildcards
abstract class GithubDao {

    @Query("DELETE FROM repositories")
    abstract suspend fun clearAllRepositories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("SELECT * FROM repositories")
    abstract fun searchRepositoriesPaged(): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE fullName = :repoName")
    abstract suspend fun getRepositoryByName(repoName: String): RepositoryEntity?

}