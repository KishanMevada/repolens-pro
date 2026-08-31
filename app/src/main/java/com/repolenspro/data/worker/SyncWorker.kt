package com.repolenspro.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.repolenspro.data.local.dao.GithubDao
import com.repolenspro.data.model.GithubApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: GithubApi,
    private val dao: GithubDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            Log.d("SyncWorker", "Background Sync Started!")

            // ૧. ડેટાબેઝમાંથી બધા ફેવરિટ્સ લાવો (One-Shot)
            val bookmarkedRepos = dao.getBookmarkedRepositoriesSync()

            if (bookmarkedRepos.isEmpty()) {
                Log.d("SyncWorker", "No bookmarks to sync.")
                return Result.success()
            }

            for (repo in bookmarkedRepos) {

                // fullName હંમેશા "owner/repo" ફોર્મેટમાં હોય છે (દા.ત. "google/gson")
                val parts = repo.fullName.split("/")
                if (parts.size == 2) {

                    val owner = parts[0]
                    val repoName = parts[1]

                    try {

                        // GitHub પરથી લેટેસ્ટ ડેટા મંગાવો
                        val latestData = api.getRepository(owner, repoName)
                        dao.updateRepoStats(repo.id, latestData.stars, latestData.forks)
                        Log.d("SyncWorker", "Updated ${repo.fullName}: ${latestData.stars} stars")

                    } catch (e: Exception) {
                        Log.e("SyncWorker", "Failed to sync ${repo.fullName}", e)
                        // જો એક રેપોમાં એરર આવે, તો લૂપ ચાલુ રાખો બીજા માટે
                    }
                }
            }


            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "SyncWorker failed", e)
            Result.retry() // જો ઇન્ટરનેટ અચાનક બંધ થાય તો WorkManager થોડીવાર પછી ફરી ટ્રાય કરશે
        }

    }

}