package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity

interface AdActions {

    suspend fun showInterstitial(
        activity: Activity,
        adUnitId: String,
        onClose: () -> Unit,
        onError: (Throwable) -> Unit,
    )

    suspend fun preloadInterstitial(
        adUnitId: String,
        onError: (Throwable) -> Unit,
    )

    suspend fun showRewarded(
        activity: Activity,
        adUnitId: String,
        onRewarded: () -> Unit,
        onAdClosed: () -> Unit,
        onError: (Throwable) -> Unit,
    )

    suspend fun preloadRewarded(
        adUnitId: String,
        onError: (Throwable) -> Unit,
    )

    fun showToast(
        activity: Activity?,
        message: String,
    )
}
