package com.repolenspro.core.network

import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    val id: Int,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    val language: String?
)

data class SearchResponseDto(
    @SerializedName("total_count") val totalCount: Int,
    val items: List<RepositoryDto>
)