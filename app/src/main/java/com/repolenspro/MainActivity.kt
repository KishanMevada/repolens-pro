package com.repolenspro

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.repolenspro.ui.navigation.AppNavGraph
import com.repolenspro.ui.theme.RepoLensProTheme
import com.repolenspro.ui.theme.ThemeViewModel
import com.repolenspro.util.AppPermissionHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ ⚠️ WorkManager નું શિડ્યુલિંગ
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // માત્ર ઇન્ટરનેટ હોય ત્યારે
            .setRequiresBatteryNotLow(true) // બેટરી ઓછી હોય ત્યારે નહિ
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<com.repolenspro.data.worker.SyncWorker>(
            24, TimeUnit.HOURS // દર 24 કલાકે
        ).setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "BookmarksSyncWork",
            ExistingPeriodicWorkPolicy.KEEP, // જો પહેલેથી શિડ્યુલ હોય તો ફરી નવું ના બનાવે
            syncWorkRequest
        )

        setContent {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                AppPermissionHandler(
                    permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                    onAllGranted = {
                        Log.d("Permission", "Notification Permission Granted! \uD83C\uDF89")
                    },
                    onDenied = { deniedList ->
                        Log.e("Permission", "User denied: $deniedList")
                    }
                )
            }

            // ThemeViewModel માંથી થીમ નો ડેટા લાવો
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            RepoLensProTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(themeViewModel = themeViewModel)
                }
            }
        }
    }
}