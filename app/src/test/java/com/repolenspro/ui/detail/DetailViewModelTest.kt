package com.repolenspro.ui.detail

import com.repolenspro.core.database.RepositoryEntity
import com.repolenspro.core.database.GithubDao
import com.repolenspro.feature.detail.DetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: GithubDao
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // relaxed = true રાખવાથી અમુક ફંક્શન જાતે જ ડમી રિટર્ન કરી દે છે
        dao = mockk(relaxed = true)
        viewModel = DetailViewModel(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchRepositoryDetails updates state correctly`() = runTest {
        // Arrange (ડમી ડેટા બનાવો)
        val mockEntity = RepositoryEntity(
            id = 1, name = "kotlin", fullName = "JetBrains/kotlin",
            description = "desc", stars = 100, forks = 50,
            language = "Kotlin", isBookmarked = true, searchQuery = "kotlin"
        )
        coEvery { dao.getRepositoryByName("JetBrains/kotlin") } returns mockEntity

        // Act (એક્શન કરો)
        viewModel.fetchRepositoryDetails("JetBrains/kotlin")
        advanceUntilIdle()

        // Assert (પરિણામ ચેક કરો)
        val state = viewModel.repository.value
        assertEquals("JetBrains/kotlin", state?.fullName)
        assertEquals(true, state?.isBookmarked)
    }

    @Test
    fun `toggleBookmark updates database and fetches latest data`() = runTest {

        // Act
        viewModel.toggleBookmark(repoId = 1, isCurrentlyBookmarked = false, repoName = "JetBrains/kotlin")
        advanceUntilIdle()

        // Assert (ખાતરી કરો કે DAO નું અપડેટ ફંક્શન સાચી વેલ્યુ સાથે બોલાવાયું હતું)
        coVerify { dao.updateBookmarkStatus(repoId = 1, isBookmarked = true) }
        coVerify { dao.getRepositoryByName("JetBrains/kotlin") }
    }

}