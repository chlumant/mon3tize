package cz.cvut.fit.chlumant.mon3tize.freemium

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger
import kotlinx.coroutines.tasks.await
import kotlin.collections.get
import kotlin.time.Duration


internal class FreemiumManager(
    private val configuration: Mon3tizeConfiguration.Freemium
) : FreemiumActions {

    private val firestore get() = Firebase.firestore
    private val firebaseAuth get() = Firebase.auth

    override val auth: FirebaseAuthActions = AuthManager

    override suspend fun canActivateTrial(): Boolean {
        checkFreemiumIsEnabled()
        val info = getFreemiumInfo() ?: return true
        return !info.trialUsed && !info.active
    }

    override suspend fun resetTrialUsed() {
        checkFreemiumIsEnabled()
        val info = FreemiumInfo(
            active = false,
            activatedAt = 0,
            expiresAt = 0,
            trialUsed = false
        )
        saveFreemiumInfo(info)
    }

    private fun getTrialDurationMillis(): Long {
        require(configuration is Mon3tizeConfiguration.Freemium.Enabled) {
            "Freemium is disabled, cannot determine duration"
        }
        return configuration.freemiumDuration.inWholeMilliseconds
    }

    override suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit
    ) {
        checkFreemiumIsEnabled()

        val user = firebaseAuth.currentUser
        if (user == null) {
            onNeedSignIn()
            return
        }

        if (!canActivateTrial()) {
            onAlreadyUsed()
            return
        }

        val now = System.currentTimeMillis()
        val expiresAt = now + getTrialDurationMillis()

        val info = FreemiumInfo(
            active = true,
            activatedAt = now,
            expiresAt = expiresAt,
            trialUsed = true
        )
        Mon3tizeLogger.d("FreemiumManager", "enableFreemium: $info")
        saveFreemiumInfo(info)

        onActivated()
    }

    override suspend fun disableFreemium() {
        checkFreemiumIsEnabled()
        saveFreemiumInfo(
            FreemiumInfo(
                active = false,
                activatedAt = 0,
                expiresAt = 0,
                trialUsed = true
            )
        )
    }

    override suspend fun isFreemiumCurrentlyActive(): Boolean {
        if (configuration is Mon3tizeConfiguration.Freemium.Disabled){
            return false
        }
        val info = getFreemiumInfo() ?: return false
        val now = System.currentTimeMillis()

        if (info.active && now > info.expiresAt) {
            saveFreemiumInfo(
                FreemiumInfo(
                    active = false,
                    activatedAt = info.activatedAt,
                    expiresAt = info.expiresAt,
                    trialUsed = true
                )
            )
            return false
        }
        return info.active
    }

    override suspend fun getTrialStatus(): Boolean {
        val info = getFreemiumInfo() ?: return false
        return info.trialUsed
    }

    private suspend fun getFreemiumInfo(): FreemiumInfo? {
        val user = firebaseAuth.currentUser ?: return null
        val doc = firestore.collection("users").document(user.uid).get().await()
        val data = doc.get("freemium") as? Map<*, *> ?: return null

        val info = FreemiumInfo(
            active = (data["active"]) as? Boolean == true,
            activatedAt = (data["activatedAt"] as? Number)?.toLong() ?: 0L,
            expiresAt = (data["expiresAt"] as? Number)?.toLong() ?: 0L,
            trialUsed = (data["trialUsed"]) as? Boolean == true
        )

        return info
    }

    private fun checkFreemiumIsEnabled() {
        if (configuration !is Mon3tizeConfiguration.Freemium.Enabled) {
            error("Freemium is disabled in configuration.")
        }
    }

    override suspend fun extendFreemiumBy(duration: Duration) {
        checkFreemiumIsEnabled()
        val info = getFreemiumInfo()
        val now = System.currentTimeMillis()

        val updated = if (info?.active == true && now < info.expiresAt) {
            info.copy(expiresAt = info.expiresAt + duration.inWholeMilliseconds)
        } else if (info?.active != null) {
            FreemiumInfo(
                active = true,
                activatedAt = now,
                expiresAt = now + duration.inWholeMilliseconds,
                trialUsed = info.trialUsed
            )
        } else {
            error("Freemium info is null.")
        }
        saveFreemiumInfo(updated)
    }

    override suspend fun shortenFreemiumBy(duration: Duration) {
        checkFreemiumIsEnabled()
        val info = getFreemiumInfo()
        val now = System.currentTimeMillis()

        if (info?.active == false) {
            return
        }

        val updated = if (info?.active == true && now < (info.expiresAt - duration.inWholeMilliseconds)) {
            info.copy(expiresAt = info.expiresAt - duration.inWholeMilliseconds)
        } else {
            FreemiumInfo(
                active = false,
                activatedAt = 0,
                expiresAt = 0,
                trialUsed = true
            )
        }
        saveFreemiumInfo(updated)
    }

    private suspend fun saveFreemiumInfo(info: FreemiumInfo) {
        val user = firebaseAuth.currentUser ?: return
        firestore.collection("users").document(user.uid)
            .set(mapOf("freemium" to info))
            .await()
    }
}