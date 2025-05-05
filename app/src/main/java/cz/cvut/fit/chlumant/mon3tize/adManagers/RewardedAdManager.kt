package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.util.Log
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import cz.cvut.fit.chlumant.mon3tize.util.resumeIfActive
import cz.cvut.fit.chlumant.mon3tize.util.resumeWithExceptionIfActive
import kotlinx.coroutines.suspendCancellableCoroutine

class RewardedAdManager(private val context: Context) {

    var preloadedAd: PreloadedAd? = null

    data class PreloadedAd(val ad: RewardedAd, val adUnitId: String)

    suspend fun preload(adUnitId: String, onError: (Throwable) -> Unit) {
        try {
            preloadedAd = PreloadedAd(loadAd(adUnitId), adUnitId)
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun loadAd(adUnitId: String): RewardedAd {
        return suspendCancellableCoroutine { continuation ->
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("RewardedAdManager", "Rewarded ad loaded")
                    continuation.resumeIfActive(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("RewardedAdManager", "Rewarded ad failed to load: ${error.message}")
                    continuation.resumeWithExceptionIfActive(
                        IllegalStateException(error.message)
                    )
                }
            })
        }
    }
    private fun RewardedAd.setCallback(onAdClosed: () -> Unit, onError: (Throwable) -> Unit) {
        fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("RewardedAdManager", "Rewarded ad dismissed")
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e("RewardedAdManager", "Rewarded ad failed to show: ${error.message}")
                onError(IllegalStateException(error.message))
            }
        }
    }

    suspend fun showAd(
        activity: Activity,
        adUnitId: String,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val ad = preloadedAd?.ad?.takeIf { it.adUnitId == adUnitId } ?: loadAd(adUnitId)
            ad.setCallback(onAdClosed, onError)
//            TODO: Implement reward + nejsem si jistej jak funguje
            ad.show(activity) { rewardItem ->
                Log.d("RewardedAdManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
            preload(adUnitId, onError)
        } catch (e: Exception) {
            Log.d("RewardedAdManager", "Error while showing rewarded ad: $e")
            onError(e)
        }
    }
}



