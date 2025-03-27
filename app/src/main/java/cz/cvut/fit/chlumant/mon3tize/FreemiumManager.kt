package cz.cvut.fit.chlumant.mon3tize

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("mon3tize_prefs")

class FreemiumManager(private val context: Context) {

    private val FREEMIUM_KEY = booleanPreferencesKey("freemium_enabled")
    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")

    val isFreemiumEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FREEMIUM_KEY] ?: false }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FIRST_LAUNCH_KEY] ?: true }

    suspend fun enableFreemium() {
        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = true
        }
    }

    suspend fun disableFreemium() {
        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = false
        }
    }

    suspend fun setFirstLaunch(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LAUNCH_KEY] = value
        }
    }
}