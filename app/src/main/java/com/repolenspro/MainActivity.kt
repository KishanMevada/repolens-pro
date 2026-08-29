package com.repolenspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.repolenspro.ui.search.SearchScreen
import com.repolenspro.ui.theme.RepoLensProTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RepoLensProTheme {
                SearchScreen()
            }
        }
    }
}