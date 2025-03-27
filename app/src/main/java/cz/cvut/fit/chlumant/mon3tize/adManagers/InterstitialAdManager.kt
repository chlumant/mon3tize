package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val activity: Activity, private val adUnitId: String) {
    private var interstitialAd: InterstitialAd? = null
    private var onAdClosed: (() -> Unit)? = null

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d("InterstitialAdManager", "Interstitial ad loaded")
                interstitialAd = ad
                setFullScreenContentCallback()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("InterstitialAdManager", "Ad failed to load: ${error.message}")
                interstitialAd = null
            }
        })
    }

    private fun setFullScreenContentCallback() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("InterstitialAdManager", "Interstitial ad dismissed")
                interstitialAd = null
                loadAd()
                onAdClosed?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.e("InterstitialAdManager", "Ad failed to show: ${p0.message}")
                interstitialAd = null
                onAdClosed?.invoke()
            }
        }
    }

    fun showAd(onAdClosed: () -> Unit) {
        if (interstitialAd != null) {
            this.onAdClosed = onAdClosed
            interstitialAd?.show(activity)
            Log.d("InterstitialAdManager", "Interstitial ad displayed")
        } else {
            Log.d("InterstitialAdManager", "Interstitial ad not ready yet")
            loadAd()
            onAdClosed()
        }
    }
}