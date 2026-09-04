package com.repolenspro.feature.search

import androidx.paging.PagingData
import com.repolenspro.core.domain.GithubRepository
import com.repolenspro.core.domain.Repository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: GithubRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `query change updates searchQuery`() = runTest {
        // Act
        viewModel.onQueryChanged("Kotlin")

        // Assert
        TestCase.assertEquals("Kotlin", viewModel.searchQuery.value)
    }

    @Test
    fun `onSearchClicked triggers pagedRepositories`() = runTest {
        // 1. Arrange
        val mockPagingData = PagingData.from(
            listOf(
                Repository(
                    1,
                    "Kotlin",
                    "JetBrains/Kotlin",
                    "Awesome language",
                    100,
                    50,
                    "Kotlin"
                )
            )
        )
        every { repository.searchRepositoriesPaged("Kotlin") } returns flowOf(mockPagingData)

        // Act
        viewModel.onQueryChanged("Kotlin")
        viewModel.onSearchClicked()

        // Assert
        val results = mutableListOf<PagingData<Repository>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pagedRepositories.collect {
                results.add(it)
            }
        }

        advanceUntilIdle()

        assert(results.isNotEmpty())
        job.cancel()
    }
}