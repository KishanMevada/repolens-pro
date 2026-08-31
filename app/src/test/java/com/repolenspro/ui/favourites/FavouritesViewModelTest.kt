package com.repolenspro.ui.favourites

import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import io.mockk.every
import io.mockk.mockk
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: GithubDao

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favourites flow emits bookmarked repositories correctly`() = runTest {
        // Arrange
        val mockList = listOf(
            RepositoryEntity(
                id = 1, name = "kotlin", fullName = "JetBrains/kotlin",
                description = "desc", stars = 100, forks = 50,
                language = "Kotlin", isBookmarked = true, searchQuery = ""
            )
        )
        // ડેટાબેઝમાંથી ફ્લો મોકલી રહ્યા છીએ
        every { dao.getBookmarkedRepositories() } returns flowOf(mockList)

        val viewModel = FavouritesViewModel(dao)
        // ✅ ફિક્સ: ડમી કલેક્ટર ઉમેર્યું. આનાથી WhileSubscribed ટ્રિગર થશે!
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.favourites.collect { }
        }

        // Act (ViewModel બનાવતા જ તે DAO ને કોલ કરશે)
        advanceUntilIdle()

        // Assert
        val state = viewModel.favourites.value
        assertEquals(1, state.size)
        assertEquals("JetBrains/kotlin", state[0].fullName)
    }

}