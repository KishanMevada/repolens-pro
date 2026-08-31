package com.repolenspro.data.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.repolenspro.data.local.RepositoryEntity
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import com.repolenspro.data.model.RepositoryDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.mockk.spyk

@OptIn(ExperimentalCoroutinesApi::class)
class SyncWorkerTest {

    private lateinit var dao: GithubDao
    private lateinit var api: GithubApi
    private lateinit var worker: SyncWorker

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        api = mockk(relaxed = true)

        // એન્ડ્રોઇડ સિસ્ટમના ક્લાસને Mock કરી દીધા જેથી ટેસ્ટ ક્રેશ ન થાય
        val context = mockk<Context>(relaxed = true)
        val workerParams = mockk<WorkerParameters>(relaxed = true)

        // 1. Spyk નો ઉપયોગ કરીને Worker નો ઓબ્જેક્ટ બનાવો (જેથી પ્રાઈવેટ ફંક્શન Mock થઈ શકે)
        worker = spyk(SyncWorker(context, workerParams, api, dao), recordPrivateCalls = true)

        // 2. private "showNotification" ફંક્શનને બાયપાસ (Mock) કરો
        every { worker["showNotification"](any<Int>()) } returns Unit
    }

    @Test
    fun `doWork returns success when no bookmarks exist`() = runTest {
        // Arrange: ડેટાબેઝ ખાલી છે
        coEvery { dao.getBookmarkedRepositoriesSync() } returns emptyList()

        // Act: વર્કર ચાલુ કરો
        val result = worker.doWork()

        // Assert: વર્કર તરત જ Success રિટર્ન કરવું જોઈએ (કોઈ API કૉલ વગર)
        assertEquals(Result.success(), result)
        coVerify(exactly = 0) { api.getRepository(any(), any()) }
    }

    @Test
    fun `doWork fetches new data and updates database correctly`() = runTest {
        // Arrange: ડેટાબેઝમાં 1 જૂનો બુકમાર્ક છે
        val mockEntity = RepositoryEntity(
            id = 1, name = "gson", fullName = "google/gson",
            description = "desc", stars = 100, forks = 50,
            language = "Java", isBookmarked = true, searchQuery = ""
        )
        coEvery { dao.getBookmarkedRepositoriesSync() } returns listOf(mockEntity)

        // API માંથી નવા સ્ટાર્સ (500) અને ફોર્ક્સ (200) આવે છે
        val mockDto = RepositoryDto(
            id = 1, name = "gson", fullName = "google/gson",
            description = "desc", stars = 500, forks = 200, language = "Java"
        )
        coEvery { api.getRepository("google", "gson") } returns mockDto

        // Act: વર્કર ચાલુ કરો
        val result = worker.doWork()

        // Assert: વર્કર પાસ થવું જોઈએ અને ડેટાબેઝ અપડેટ થવો જોઈએ
        assertEquals(Result.success(), result)
        coVerify { dao.updateRepoStats(id = 1, stars = 500, forks = 200) }
    }

}