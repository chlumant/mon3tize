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
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger
import cz.cvut.fit.chlumant.mon3tize.util.resumeIfActive
import cz.cvut.fit.chlumant.mon3tize.util.resumeWithExceptionIfActive
import kotlinx.coroutines.suspendCancellableCoroutine

public class InterstitialAdManager(private val context: Context) {

    private var preloadedAd: PreloadedAd? = null

    private data class PreloadedAd(val ad: InterstitialAd, val adUnitId: String)

    public suspend fun preload(adUnitId: String, onError: (Throwable) -> Unit) {
        try {
            preloadedAd = PreloadedAd(loadAd(adUnitId), adUnitId)
        } catch (e: Exception) {
            onError(e)
        }
    }

    public suspend fun loadAd(adUnitId: String): InterstitialAd {
        return suspendCancellableCoroutine { continuation ->
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Mon3tizeLogger.d("InterstitialAdManager", "Interstitial ad loaded")
                    continuation.resumeIfActive(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Mon3tizeLogger.e("InterstitialAdManager", "Ad failed to load: ${error.message}")
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
                Mon3tizeLogger.d("InterstitialAdManager", "Interstitial ad dismissed")
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Mon3tizeLogger.e("InterstitialAdManager", "Ad failed to show: ${error.message}")
                onError(IllegalStateException(error.message))
            }
        }
    }

    public suspend fun showAd(activity: Activity, adUnitId: String, onAdClosed: () -> Unit, onError: (Throwable) -> Unit) {
        try {
            val ad = preloadedAd?.ad?.takeIf { it.adUnitId == adUnitId } ?: loadAd(adUnitId)
            ad.setCallback(onAdClosed, onError)
            ad.show(activity)
            Mon3tizeLogger.d("InterstitialAdManager", "Interstitial ad displayed")
            preload(adUnitId, onError)
        } catch (e: Exception) {
            Mon3tizeLogger.d("InterstitialAdManager", "Error while showing ad $e")
            onError(e)
        }
    }
}