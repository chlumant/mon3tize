package cz.cvut.fit.chlumant.mon3tize.adManagers

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.api.Context

class RewardedAdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private var onRewardEarned: (() -> Unit)? = null

    init {
        loadAd()
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("RewardedAdManager", "Rewarded ad loaded successfully")
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("RewardedAdManager", "Rewarded ad failed to load: ${error.message}")
                rewardedAd = null
            }
        })
    }

    fun showAd(onRewardEarned: () -> Unit) {
        if (rewardedAd != null) {
            this.onRewardEarned = onRewardEarned
            rewardedAd?.show(context) { rewardItem ->
                Log.d("RewardedAdManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
            rewardedAd = null
            loadAd()
        } else {
            Log.d("RewardedAdManager", "Rewarded ad not ready yet, loading new ad")
            loadAd()
        }
    }
}