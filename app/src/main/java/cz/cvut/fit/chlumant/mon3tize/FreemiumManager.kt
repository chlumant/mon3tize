package cz.cvut.fit.chlumant.mon3tize

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private val Context.dataStore by preferencesDataStore("mon3tize_prefs")

class FreemiumManager(private val context: Context) {

    private val FREEMIUM_KEY = booleanPreferencesKey("freemium_supported")
    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    val isFreemiumEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FREEMIUM_KEY] == true }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FIRST_LAUNCH_KEY] != false }

    suspend fun setFirstLaunch(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LAUNCH_KEY] = value
        }

        val currentInfo = getFreemiumInfo() ?: FreemiumInfo()
        val updatedInfo = currentInfo.copy(isFirst = value)
        saveFreemiumInfo(updatedInfo)
    }

    suspend fun enableFreemium() {
        val now = System.currentTimeMillis()
        val weekFromNow = now + 7 * 24 * 60 * 60 * 1000L // 7 day trial

        val info = FreemiumInfo(
            isActive = true,
            activatedAt = now,
            expiresAt = weekFromNow,
            isFirst = false
        )

        saveFreemiumInfo(info)

        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = true
        }
    }

    suspend fun disableFreemium() {
        saveFreemiumInfo(
            FreemiumInfo(
                isActive = false,
                activatedAt = 0,
                expiresAt = 0,
                isFirst = false
            )
        )

        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = false
        }
    }

    suspend fun synchronizeWithFirebase() {
        try {
            val info = getFreemiumInfo()
            context.dataStore.edit { prefs ->
                prefs[FREEMIUM_KEY] = info?.isActive == true
            }
        } catch (_: Exception) {

        }
    }

    suspend fun isFreemiumCurrentlyActive(): Boolean {
        return getFreemiumInfo()?.let {
            it.isActive && System.currentTimeMillis() <= it.expiresAt
        } == true
    }

    suspend fun getFreemiumInfo(): FreemiumInfo? {
        val user = auth.currentUser ?: auth.signInAnonymously().await().user ?: return null
        val doc = firestore.collection("users").document(user.uid).get().await()
        val data = doc.get("freemium") as? Map<*, *> ?: return null

        return FreemiumInfo(
            isActive = data["isActive"] as? Boolean == true,
            activatedAt = (data["activatedAt"] as? Number)?.toLong() ?: 0L,
            expiresAt = (data["expiresAt"] as? Number)?.toLong() ?: 0L,
            isFirst = (data["isFirst"]) as? Boolean == true
        )
    }

    suspend fun saveFreemiumInfo(info: FreemiumInfo) {
        val user = auth.currentUser ?: auth.signInAnonymously().await().user ?: return
        val data = mapOf("freemium" to info)
        firestore.collection("users").document(user.uid).set(data).await()
    }
}
