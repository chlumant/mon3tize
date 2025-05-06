package cz.cvut.fit.chlumant.mon3tize.billing

import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger

@Suppress("DEPRECATION")
class PurchaseListener(private val context: Context) : PurchasesUpdatedListener {

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {

            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Mon3tizeLogger.d(TAG, "User canceled the purchase.")
        } else {
            Mon3tizeLogger.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    billingClient.acknowledgePurchase(params) { ackResult ->
                        if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Mon3tizeLogger.d(TAG, "Purchase acknowledged.")
                        } else {
                            Mon3tizeLogger.e(TAG, "Failed to acknowledge purchase: ${ackResult.debugMessage}")
                        }
                        billingClient.endConnection()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected while acknowledging purchase.")
            }
        })
    }

    companion object {
        private const val TAG = "PurchaseListener"
    }
}
