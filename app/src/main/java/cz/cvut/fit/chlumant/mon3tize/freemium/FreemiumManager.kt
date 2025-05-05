package cz.cvut.fit.chlumant.mon3tize.freemium

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import kotlinx.coroutines.tasks.await
import kotlin.collections.get


internal class FreemiumManager(
    private val configuration: Mon3tizeConfiguration.Freemium
) : FreemiumActions {

    private val firestore get() = Firebase.firestore
    private val auth get() = Firebase.auth

    override suspend fun canActivateTrial(): Boolean {
        checkFreemiumIsEnabled()
        val info = getFreemiumInfo() ?: return true
        return !info.trialUsed && !info.isActive
    }

    override suspend fun resetTrialUsed() {
        checkFreemiumIsEnabled()
        val info = FreemiumInfo(
            isActive = false,
            activatedAt = 0,
            expiresAt = 0,
            trialUsed = false
        )
        saveFreemiumInfo(info)
    }

    //TODO: nejsem si jistej jestli spravne pracuju s tim configem
    private fun getTrialDurationMillis(): Long {
        require(configuration is Mon3tizeConfiguration.Freemium.Enabled) {
            "Freemium is disabled, cannot determine duration"
        }
        return configuration.freemiumDuration.inWholeMilliseconds
    }

    //TODO: mozna pridat ze uz je momentalne aktivni?
    override suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit
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
        val expiresAt = now + getTrialDurationMillis()

        val info = FreemiumInfo(
            isActive = true,
            activatedAt = now,
            expiresAt = expiresAt,
            trialUsed = true
        )
        saveFreemiumInfo(info)

        onActivated()
    }

    override suspend fun disableFreemium() {
        checkFreemiumIsEnabled()
        saveFreemiumInfo(
            FreemiumInfo(
                isActive = false,
                activatedAt = 0,
                expiresAt = 0,
                trialUsed = true
            )
        )
    }

    //TODO; jak checkovat to uplynuti trialu v aplikaci
    override suspend fun isFreemiumCurrentlyActive(): Boolean {
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

    override suspend fun getFreemiumInfo(): FreemiumInfo? {
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

    //TODO: tohle tady takhle nechat uvnitr vsech tech metod?
    private fun checkFreemiumIsEnabled() {
        if (configuration !is Mon3tizeConfiguration.Freemium.Enabled) {
            error("Freemium is disabled in configuration.")
        }
    }

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
