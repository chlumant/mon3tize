package cz.cvut.fit.chlumant.mon3tize.freemium

import kotlin.time.Duration

public interface FreemiumActions {

    //TODO: TOHLE MA BYT TADY?
    public val auth: FirebaseAuthActions

    public suspend fun canActivateTrial(): Boolean

    public suspend fun enableFreemium(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit,
        onAlreadyUsed: () -> Unit
    )

    public suspend fun disableFreemium()

    public suspend fun isFreemiumCurrentlyActive(): Boolean

    public suspend fun getTrialStatus(): Boolean

    public suspend fun resetTrialUsed()

    public suspend fun extendFreemiumBy(duration: Duration)

    public suspend fun shortenFreemiumBy(duration: Duration)
}
