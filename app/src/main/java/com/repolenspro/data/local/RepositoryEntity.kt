package com.repolenspro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity (
    @PrimaryKey val id: Int = 0,
    val name: String,
    val fullName: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val language: String
)