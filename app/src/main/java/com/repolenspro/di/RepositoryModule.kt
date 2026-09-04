package com.repolenspro.di

import com.repolenspro.data.repository.GithubRepositoryImpl
import com.repolenspro.core.domain.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
}
