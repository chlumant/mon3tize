package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.collections.get


private val Context.dataStore by preferencesDataStore("mon3tize_prefs")


internal class FreemiumManager(
    private val context: Context,
    private val configuration: Mon3tizeConfiguration.Freemium
) {
    private val FREEMIUM_KEY = booleanPreferencesKey("freemium_active")
    private val TRIAL_USED = booleanPreferencesKey("free_trial_used")

    private val firestore get() = Firebase.firestore
    private val auth get() = Firebase.auth

    val isFreemiumActive: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FREEMIUM_KEY] == true }

    suspend fun canActivateTrial(): Boolean {
        checkFreemiumIsEnabled()
        val info = getFreemiumInfo() ?: return true
        return !info.trialUsed
    }

    suspend fun resetTrialUsed() {
        checkFreemiumIsEnabled()
        val info = FreemiumInfo(
            isActive = false,
            activatedAt = 0,
            expiresAt = 0,
            trialUsed = false
        )
        saveFreemiumInfo(info)
    }

    suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit = {}
    ) {
        checkFreemiumIsEnabled()

        val user = auth.currentUser
        if (user == null) {
            onNeedSignIn()
            return
        }

        if (!canActivateTrial()) {
            onAlreadyUsed()
            return
        }

        val now = System.currentTimeMillis()
        val expiresAt = now + configuration.freemiumDuration.inWholeMilliseconds

        val info = FreemiumInfo(
            isActive = true,
            activatedAt = now,
            expiresAt = expiresAt,
            trialUsed = true
        )
        saveFreemiumInfo(info)

        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = true
        }
        context.dataStore.edit { prefs ->
            prefs[TRIAL_USED] = true
        }

        onActivated()
    }

    suspend fun disableFreemium() {
        checkFreemiumIsEnabled()
        saveFreemiumInfo(
            FreemiumInfo(
                isActive = false,
                activatedAt = 0,
                expiresAt = 0,
                trialUsed = true
            )
        )

        context.dataStore.edit { prefs ->
            prefs[FREEMIUM_KEY] = false
        }
    }

    // Pujde do prdele
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
        if (configuration is Mon3tizeConfiguration.Freemium.Disabled) return false
        val info = getFreemiumInfo() ?: return false
        val now = System.currentTimeMillis()

        if (info.isActive && now > info.expiresAt) {
            saveFreemiumInfo(
                FreemiumInfo(
                    isActive = false,
                    activatedAt = info.activatedAt,
                    expiresAt = info.expiresAt,
                    trialUsed = true
                )
            )
            return false
        }
        return info.isActive
    }

    suspend fun getFreemiumInfo(): FreemiumInfo? {
        val user = auth.currentUser ?: return null
        val doc = firestore.collection("users").document(user.uid).get().await()
        val data = doc.get("freemium") as? Map<*, *> ?: return null

        return FreemiumInfo(
            isActive = data["isActive"] as? Boolean == true,
            activatedAt = (data["activatedAt"] as? Number)?.toLong() ?: 0L,
            expiresAt = (data["expiresAt"] as? Number)?.toLong() ?: 0L,
            trialUsed = (data["trialUsed"]) as? Boolean == true
        )
    }

    private fun checkFreemiumIsEnabled() {
        if (configuration !is Mon3tizeConfiguration.Freemium.Enabled) {
            error("Freemium is disabled in configuration.")
        }
    }

//    fun checkPremiumAccessWithTrialControl(
//        subscriptionProductId: String,
//        onAccessGranted: () -> Unit,
//        onTrialExpired: () -> Unit,
//        onNotSignedIn: () -> Unit = {},
//    ) {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            onNotSignedIn()
//            return
//        }
//
//        CoroutineScope(Dispatchers.Main).launch {
//            val hasTrial = isFreemiumCurrentlyActive()
//            val trialUsed = getFreemiumInfo()?.trialUsed == true
//
//            val hasSubscription = suspendCoroutine<Boolean> { continuation ->
//                Mon3tize.billingManager.checkActiveSubscription(subscriptionProductId) {
//                    continuation.resume(it)
//                }
//            }
//
//            when {
//                hasTrial || hasSubscription -> onAccessGranted()
//                trialUsed -> onTrialExpired()
//                else -> onNotSignedIn()
//            }
//        }
//    }


    private suspend fun saveFreemiumInfo(info: FreemiumInfo) {
        val user = auth.currentUser ?: return
        firestore.collection("users").document(user.uid)
            .update("freemium", info)
            .addOnFailureListener {
                firestore.collection("users").document(user.uid).set(mapOf("freemium" to info))
            }
            .await()
    }
}
