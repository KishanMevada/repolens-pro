package com.repolenspro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.repolenspro.data.local.dao.GithubDao

@Database(entities = [RepositoryEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun githubDao(): GithubDao
}