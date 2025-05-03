package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.content.Context

/**
 * TODO add class description
 */
class AdManager(private val context: Context) : AdActions {

    private val interstitialAdManager = InterstitialAdManager(context)
    private val rewardedAdManager = RewardedAdManager(context)

    override suspend fun showInterstitial(
        activity: Activity,
        adUnitId: String,
        onClose: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        interstitialAdManager.showAd(activity, adUnitId, onClose, onError)
    }

    override suspend fun preloadInterstitial(
        adUnitId: String,
        onError: (Throwable) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun showRewarded(
        activity: Activity,
        adUnitId: String,
        onRewarded: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun preloadRewarded(
        adUnitId: String,
        onError: (Throwable) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}