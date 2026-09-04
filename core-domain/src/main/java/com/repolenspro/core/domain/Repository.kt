package com.repolenspro.core.domain

data class Repository(
    val id: Int,
    val name: String,
    val fullName: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val language: String,
    val isBookmarked: Boolean = false
)