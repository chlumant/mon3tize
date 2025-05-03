package cz.cvut.fit.chlumant.mon3tize

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.firebase.FirebaseApp
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdActions
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdManager
import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager
import cz.cvut.fit.chlumant.mon3tize.billing.PurchaseListener
import cz.cvut.fit.chlumant.mon3tize.freemium.FreemiumManager
import cz.cvut.fit.chlumant.mon3tize.util.AppContextHolder

object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

    @SuppressLint("StaticFieldLeak")
    private lateinit var freemiumManager: FreemiumManager

    val freemium: FreemiumActions get() = freemiumManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var billingManager: BillingManager

    val billing: BillingActions get() = billingManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var adManager: AdManager

    val ads: AdActions get() = adManager

    //  pridat neco pokud nebudu chtit mit nakupy v aplikaci?
    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration
        AppContextHolder.init(context)

        this.billingManager = BillingManager(
            context = context.applicationContext,
            listener = PurchaseListener
        )

        this.adManager = AdManager(context)

        this.freemiumManager =
            FreemiumManager(context = context.applicationContext, configuration.freemium)
        FirebaseApp.initializeApp(context)

        if (configuration.adMobEnabled) validateAdMobManifestConfig(context)
    }


    suspend fun isPremiumAccessAvailable(subscriptionProductId: String): Boolean {
        val hasActiveTrial = freemiumManager.isFreemiumCurrentlyActive()
        val hasActiveSubscription = billingManager.isSubscriptionActive(subscriptionProductId)
        return hasActiveTrial || hasActiveSubscription
    }

    private fun validateAdMobManifestConfig(context: Context) {
        val appId = try {
            val ai = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            ai.metaData?.getString("com.google.android.gms.ads.APPLICATION_ID")
        } catch (_: Exception) {
            null
        }

        if (appId.isNullOrBlank()) {
            Log.e("Mon3tize", "AdMob App ID is missing from AndroidManifest.xml")
        } else {
            Log.d("Mon3tize", "AdMob App ID found: $appId")
        }
    }
}
