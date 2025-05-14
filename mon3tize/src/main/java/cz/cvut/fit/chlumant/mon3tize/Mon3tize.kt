package cz.cvut.fit.chlumant.mon3tize

import android.annotation.SuppressLint
import android.content.Context
import com.android.billingclient.api.*
import com.google.firebase.FirebaseApp
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdActions
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdManager
import cz.cvut.fit.chlumant.mon3tize.billing.BillingActions
import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager
import cz.cvut.fit.chlumant.mon3tize.billing.PurchaseListener
import cz.cvut.fit.chlumant.mon3tize.freemium.AuthManager
import cz.cvut.fit.chlumant.mon3tize.freemium.FirebaseAuthActions
import cz.cvut.fit.chlumant.mon3tize.freemium.FreemiumActions
import cz.cvut.fit.chlumant.mon3tize.freemium.FreemiumManager
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

    @SuppressLint("StaticFieldLeak")
    private lateinit var freemiumManager: FreemiumManager

    public val freemium: FreemiumActions get() = freemiumManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var billingManager: BillingManager

    public val billing: BillingActions get() = billingManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var adManager: AdManager

    public val ads: AdActions get() = adManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var authManager: AuthManager

    public val auth: FirebaseAuthActions get() = authManager

    internal val logLevel: Mon3tizeConfiguration.LogLevel? get() = this.configuration?.logLevel

    internal val clientId: String? get() = configuration?.clientId


    public fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration

        val purchaseListener = PurchaseListener()

        val billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(purchaseListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        purchaseListener.setBillingClient(billingClient)

        this.billingManager = BillingManager(
            billingClient = billingClient
        )

        this.adManager = AdManager(context)
        this.freemiumManager = FreemiumManager(configuration.freemium)

        FirebaseApp.initializeApp(context)
        CoroutineScope(Dispatchers.IO).launch {
            billingManager.startConnection()
        }
    }

    public suspend fun isPremiumAccessAvailable(subscriptionProductId: String): Boolean {

        val hasActiveSubscription = billing.isSubscriptionActive(subscriptionProductId)

        if (configuration?.freemium is Mon3tizeConfiguration.Freemium.Enabled) {
            val hasActiveTrial = freemium.isFreemiumCurrentlyActive()
            Mon3tizeLogger.d("Mon3tize", "isPremiumAccessAvailable: $hasActiveTrial")
            return hasActiveTrial || hasActiveSubscription
        } else {
            return hasActiveSubscription
        }
    }
}