package cz.cvut.fit.chlumant.mon3tize.billing


import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger


internal class PurchaseListener : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    
    fun setBillingClient(client: BillingClient) {
        this.billingClient = client
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Mon3tizeLogger.d(TAG, "User canceled the purchase.")
        } else {
            Mon3tizeLogger.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        billingClient?.let { client ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            client.acknowledgePurchase(params) { ackResult ->
                if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Mon3tizeLogger.d(TAG, "Purchase acknowledged.")
                } else {
                    Mon3tizeLogger.e(TAG, "Failed to acknowledge purchase: ${ackResult.debugMessage}")
                }
            }
        } ?: Mon3tizeLogger.e(TAG, "BillingClient is not set.")
    }

    private companion object {
        private const val TAG = "PurchaseListener"
    }
}
