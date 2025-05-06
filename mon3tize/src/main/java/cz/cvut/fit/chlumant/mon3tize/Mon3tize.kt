package cz.cvut.fit.chlumant.mon3tize

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.FirebaseApp
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdActions
import cz.cvut.fit.chlumant.mon3tize.adManagers.AdManager
import cz.cvut.fit.chlumant.mon3tize.billing.BillingActions
import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager
import cz.cvut.fit.chlumant.mon3tize.billing.PurchaseListener
import cz.cvut.fit.chlumant.mon3tize.freemium.FreemiumActions
import cz.cvut.fit.chlumant.mon3tize.freemium.FreemiumManager

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

    internal val logLevel: Mon3tizeConfiguration.LogLevel? get() = this.configuration?.logLevel

    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration

        this.billingManager = BillingManager(
            context = context.applicationContext,
            listener = PurchaseListener(context)
        )

        this.adManager = AdManager(context)

        this.freemiumManager = FreemiumManager(configuration.freemium)
        FirebaseApp.initializeApp(context)
    }

    suspend fun isPremiumAccessAvailable(subscriptionProductId: String): Boolean {

        val hasActiveSubscription = billing.isSubscriptionActive(subscriptionProductId)

        if (configuration?.freemium is Mon3tizeConfiguration.Freemium.Enabled) {
            val hasActiveTrial = freemium.isFreemiumCurrentlyActive()
            return hasActiveTrial || hasActiveSubscription
        } else {
            return hasActiveSubscription
        }
    }
}
