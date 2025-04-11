package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class BillingManager(
    private val context: Context,
    private val listener: PurchasesUpdatedListener
) {

    init {
        Log.d("BillingManager", "BillingClient built, version 7.1.1")
    }

    @Suppress("DEPRECATION")
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(listener)
        .enablePendingPurchases()
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
                val result = productDetailsList.firstOrNull()
                if (result == null) {
                    Log.e("BillingManager", "No matching product found for $productId")
                }
                onResult(result)
            } else {
                Log.e("BillingManager", "Query failed: ${billingResult.debugMessage}")
                onResult(null)
            }
        }
    }

    fun queryOneTimeProduct(productId: String, onResult: (ProductDetails?) -> Unit) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val result = productDetailsList.firstOrNull()
                if (result == null) {
                    Log.e("BillingManager", "One-time test product not found.")
                } else {
                    Log.d("BillingManager", "One-time test product loaded: ${result.name}")
                }
                onResult(result)
            } else {
                Log.e("BillingManager", "Query failed: ${billingResult.debugMessage}")
                onResult(null)
            }
        }
    }

    fun launchInAppPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.e("BillingManager", "Query failed: ${billingResult.responseCode} - ${billingResult.debugMessage}")

    }

    fun launchSubscriptionPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
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