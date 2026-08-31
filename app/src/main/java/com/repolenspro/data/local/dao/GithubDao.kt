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

    @Query("DELETE FROM repositories WHERE isBookmarked = 0")
    abstract suspend fun clearAllRepositories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("SELECT * FROM repositories WHERE searchQuery = :query")
    abstract fun searchRepositoriesPaged(query: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE fullName = :repoName")
    abstract suspend fun getRepositoryByName(repoName: String): RepositoryEntity?

    // ફેવરિટનું સ્ટેટસ અપડેટ કરવા માટે
    @Query("UPDATE repositories SET isBookmarked = :isBookmarked WHERE id = :repoId")
    abstract suspend fun updateBookmarkStatus(repoId: Int, isBookmarked: Boolean)

    // માત્ર ફેવરિટ્સનું લિસ્ટ લાવવા માટે (Favorites Screen માટે)
    @Query("SELECT * FROM repositories WHERE isBookmarked = 1")
    abstract fun getBookmarkedRepositories(): kotlinx.coroutines.flow.Flow<List<RepositoryEntity>>

}