package cz.cvut.fit.chlumant.mon3tize.ads

import android.app.Activity

public interface AdActions {

    public suspend fun showInterstitial(
        activity: Activity,
        adUnitId: String,
        onClose: () -> Unit,
        onError: (Throwable) -> Unit,
    )

    public suspend fun preloadInterstitial(
        adUnitId: String,
        onError: (Throwable) -> Unit,
    )

    public suspend fun showRewarded(
        activity: Activity,
        adUnitId: String,
        onRewardEarn: (AdReward) -> Unit,
        onClose: () -> Unit,
        onError: (Throwable) -> Unit,
    )

    public suspend fun preloadRewarded(
        adUnitId: String,
        onError: (Throwable) -> Unit,
    )
}