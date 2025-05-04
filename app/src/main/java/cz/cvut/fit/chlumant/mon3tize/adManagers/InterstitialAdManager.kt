package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import cz.cvut.fit.chlumant.mon3tize.util.resumeIfActive
import cz.cvut.fit.chlumant.mon3tize.util.resumeWithExceptionIfActive
import kotlinx.coroutines.suspendCancellableCoroutine

class InterstitialAdManager(private val context: Context) {

    var preloadedAd: PreloadedAd? = null

    data class PreloadedAd(val ad: InterstitialAd, val adUnitId: String)

    suspend fun preload(adUnitId: String, onError: (Throwable) -> Unit) {
        try {
            preloadedAd = PreloadedAd(loadAd(adUnitId), adUnitId)
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun loadAd(adUnitId: String): InterstitialAd {
        return suspendCancellableCoroutine { continuation ->
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("InterstitialAdManager", "Interstitial ad loaded")
                    continuation.resumeIfActive(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("InterstitialAdManager", "Ad failed to load: ${error.message}")
                    continuation.resumeWithExceptionIfActive(
                        IllegalStateException(error.message)
                    )
                }
            })
        }
    }

    private fun InterstitialAd.setCallback(onAdClosed: () -> Unit, onError: (Throwable) -> Unit) {
        fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("InterstitialAdManager", "Interstitial ad dismissed")
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e("InterstitialAdManager", "Ad failed to show: ${error.message}")
                onError(IllegalStateException(error.message))
            }
        }
    }

    suspend fun showAd(activity: Activity, adUnitId: String, onAdClosed: () -> Unit, onError: (Throwable) -> Unit) {
        try {
            val ad = preloadedAd?.ad?.takeIf { it.adUnitId == adUnitId } ?: loadAd(adUnitId)
            ad.setCallback(onAdClosed, onError)
            ad.show(activity)
            Log.d("InterstitialAdManager", "Interstitial ad displayed")
            preload(adUnitId, onError)
        } catch (e: Exception) {
            Log.d("InterstitialAdManager", "Error while showing ad $e")
            onError(e)
        }
    }
}