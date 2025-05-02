package cz.cvut.fit.chlumant.mon3tize

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.firebase.FirebaseApp
import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager
import cz.cvut.fit.chlumant.mon3tize.billing.PurchaseListener
import cz.cvut.fit.chlumant.mon3tize.util.AppContextHolder

object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

    @SuppressLint("StaticFieldLeak")
    lateinit var freemiumManager: FreemiumManager
        private set

    @SuppressLint("StaticFieldLeak")
    lateinit var billingManager: BillingManager
        private set

//  pridat neco pokud nebudu chtit mit nakupy v aplikaci?
    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration
        AppContextHolder.init(context)

        this.billingManager = BillingManager(
            context = context.applicationContext,
            listener = PurchaseListener
        )

        billingManager.startConnection {}

        if (configuration.enableFreemium) {
            this.freemiumManager = FreemiumManager(context = context.applicationContext)
            FirebaseApp.initializeApp(context)
        }

        if (configuration.adMobEnabled) validateAdMobManifestConfig(context)
    }

    private fun validateAdMobManifestConfig(context: Context) {
        val appId = try {
            val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
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
