package cz.cvut.fit.chlumant.mon3tize.freemium

import kotlin.time.Duration

interface FreemiumActions {

    suspend fun canActivateTrial(): Boolean

    suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit
    )

    suspend fun disableFreemium()

    suspend fun isFreemiumCurrentlyActive(): Boolean

    suspend fun getFreemiumInfo(): FreemiumInfo?

    suspend fun resetTrialUsed()

    suspend fun extendFreemiumBy(duration: Duration)
}
