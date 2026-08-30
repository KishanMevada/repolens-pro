package com.repolenspro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.repolenspro.data.local.RepositoryEntity

@Dao
@JvmSuppressWildcards
interface GithubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>): List<Long>

    @Query("SELECT * FROM repositories WHERE name LIKE '%' || :query || '%' OR fullName LIKE '%' || :query || '%'" )
    suspend fun searchRepositories(query: String): List<RepositoryEntity>

}