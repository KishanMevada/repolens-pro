package com.repolenspro.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore નું ઇન્સ્ટન્સ બનાવ્યું
val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // થીમ કઈ છે તે વાંચવા માટે (Flow)
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false // ડિફોલ્ટ લાઈટ મોડ (false)
    }

    // થીમ સેવ/બદલવા માટે
    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
}