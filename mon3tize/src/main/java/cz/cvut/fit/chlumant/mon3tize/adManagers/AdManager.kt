package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.content.Context

internal class AdManager(context: Context) : AdActions {

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
        interstitialAdManager.preload(adUnitId, onError)
    }

    override suspend fun showRewarded(
        activity: Activity,
        adUnitId: String,
        onRewardEarn: (AdReward) -> Unit,
        onAdClosed: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        rewardedAdManager.showAd(activity, adUnitId, onRewardEarn, onAdClosed, onError)
    }

    override suspend fun preloadRewarded(
        adUnitId: String,
        onError: (Throwable) -> Unit
    ) {
        rewardedAdManager.preload(adUnitId, onError)
    }
}
