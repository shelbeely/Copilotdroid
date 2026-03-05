package com.example.agenthq.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "host_prefs")

@Singleton
class HostPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val hostKey = stringPreferencesKey("github_host")

    val githubHost: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[hostKey] ?: DEFAULT_HOST
    }

    suspend fun setHost(host: String) {
        context.dataStore.edit { prefs ->
            prefs[hostKey] = host.trimEnd('/')
        }
    }

    suspend fun resetToDefault() = setHost(DEFAULT_HOST)

    companion object {
        const val DEFAULT_HOST = "https://github.com"
        const val DEFAULT_API_HOST = "https://api.github.com"

        fun apiHostFor(host: String): String {
            return if (host == DEFAULT_HOST || host.isBlank()) {
                DEFAULT_API_HOST
            } else {
                "$host/api/v3"  // GitHub Enterprise Server API path
            }
        }

        fun graphqlHostFor(host: String): String {
            return if (host == DEFAULT_HOST || host.isBlank()) {
                "$DEFAULT_API_HOST/graphql"
            } else {
                "$host/api/graphql"
            }
        }

        fun oauthBaseFor(host: String): String {
            return if (host == DEFAULT_HOST || host.isBlank()) DEFAULT_HOST else host
        }
    }
}
