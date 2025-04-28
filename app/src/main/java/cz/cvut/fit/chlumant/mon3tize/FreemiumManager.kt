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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

//import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager

private val Context.dataStore by preferencesDataStore("mon3tize_prefs")


class FreemiumManager(
    private val context: Context,
    private val trialDuration: Duration = 7.days
) {
    private val FREEMIUM_KEY = booleanPreferencesKey("freemium_active")
    private val TRIAL_USED = booleanPreferencesKey("free_trial_used")

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    val isFreemiumActive: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FREEMIUM_KEY] == true }

    suspend fun canActivateTrial(): Boolean {
        val info = getFreemiumInfo() ?: return true
        return !info.trialUsed
    }

    suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit = {}
    ) {
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
        val expiresAt = now + trialDuration.inWholeMilliseconds

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


//    suspend fun isPremiumAccessAvailable(billingManager: BillingManager, subscriptionProductId: String, oneTimeProductId: String): Boolean {
//        val hasActiveTrial = isFreemiumCurrentlyActive()
//        var hasActiveSubscription = false
//
//
//        val subscriptionCheck = kotlinx.coroutines.suspendCancellableCoroutine<Boolean> { continuation ->
//            billingManager.checkActiveSubscription(subscriptionProductId) { isActive ->
//                continuation.resume(isActive) {}
//            }
//        }
//
//        hasActiveSubscription = subscriptionCheck
//
//        return hasActiveTrial || hasActiveSubscription
//    }

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

    suspend fun saveFreemiumInfo(info: FreemiumInfo) {
        val user = auth.currentUser ?: return
        firestore.collection("users").document(user.uid)
            .update("freemium", info)
            .addOnFailureListener {
                firestore.collection("users").document(user.uid).set(mapOf("freemium" to info))
            }
            .await()
    }
}
