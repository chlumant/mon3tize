package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity

public interface AdActions {

    public suspend fun showInterstitial(
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
        onRewardEarn: (AdReward) -> Unit,
        onClose: () -> Unit,
        onError: (Throwable) -> Unit,
    )

    suspend fun preloadRewarded(
        adUnitId: String,
        onError: (Throwable) -> Unit,
    )
}
