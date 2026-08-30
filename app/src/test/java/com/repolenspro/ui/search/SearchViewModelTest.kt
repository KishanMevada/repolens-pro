package com.repolenspro.ui.search

import com.repolenspro.domain.model.Repository
import com.repolenspro.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
    fun `search success updates state correctly`() = runTest {
        // 1. Arrange
        val mockData = listOf(
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
        coEvery { repository.searchRepository("Kotlin") } returns Result.success(mockData)

        //Act
        viewModel.onQueryChanged("Kotlin")
        viewModel.search()
        advanceUntilIdle()

        //Assert
        val currentState = viewModel.uiState.value
        assert(!currentState.isLoading)
        assertEquals(1, currentState.repositories.size)
        assertEquals("Kotlin", currentState.repositories.first().name)
    }
}