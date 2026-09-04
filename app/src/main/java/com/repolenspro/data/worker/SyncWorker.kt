package com.repolenspro.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.repolenspro.R
import com.repolenspro.core.database.GithubDao
import com.repolenspro.core.network.GithubApi
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

            // ✅ ⚠️ લૂપ પૂરું થાય એટલે છેલ્લે આ ફંક્શનને કૉલ કરો
            showNotification(bookmarkedRepos.size)

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "SyncWorker failed", e)
            Result.retry() // જો ઇન્ટરનેટ અચાનક બંધ થાય તો WorkManager થોડીવાર પછી ફરી ટ્રાય કરશે
        }

    }

    private fun showNotification(updatedCount: Int) {

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sync_channel"

        // Android 8.0 (Oreo) થી Channel બનાવવું ફરજિયાત છે
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Background Sync",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows notifications when bookmarks are updated"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // નોટિફિકેશનની ડિઝાઇન
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Bookmarks Updated! \uD83C\uDF1F")
            .setContentText("Successfully synced $updatedCount favorite repositories.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // 1 નંબરના ID સાથે નોટિફિકેશન ફાયર કરો
        notificationManager.notify(1, notification)

    }

}
