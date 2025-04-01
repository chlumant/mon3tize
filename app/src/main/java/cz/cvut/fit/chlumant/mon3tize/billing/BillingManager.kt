package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class BillingManager(
    private val context: Context,
    private val listener: PurchasesUpdatedListener
) {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(listener)
        .build()

    fun startConnection(onReady: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "BillingClient is ready")
                    onReady()
                } else {
                    Log.e("BillingManager", "Error setting up billing: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w("BillingManager", "Billing service disconnected")
            }
        })
    }

    fun querySubscriptions(productId: String, onResult: (ProductDetails?) -> Unit) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("BillingManager", "Queried subscriptions: ${productDetailsList.size}")
                onResult(productDetailsList.firstOrNull())
            } else {
                Log.e("BillingManager", "Failed to query subscriptions: ${billingResult.debugMessage}")
                onResult(null)
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: run {
            Log.e("BillingManager", "No offer token available for product ${productDetails.productId}")
            return
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d("BillingManager", "Launch billing flow result: ${billingResult.responseCode}")
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}