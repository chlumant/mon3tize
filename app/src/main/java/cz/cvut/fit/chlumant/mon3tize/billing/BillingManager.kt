package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    fun checkActiveSubscription(productId: String, onResult: (Boolean) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasActiveSubscription = purchasesList.any { purchase ->
                    purchase.products.contains(productId) &&
                            purchase.isAcknowledged &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                onResult(hasActiveSubscription)
            } else {
                Log.e("BillingManager", "Failed to query subscriptions: ${billingResult.debugMessage}")
                onResult(false)
            }
        }
    }

    fun checkPurchasedProduct(productId: String, onResult: (Boolean) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasProduct = purchasesList.any { purchase ->
                    purchase.products.contains(productId) &&
                            purchase.isAcknowledged &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                onResult(hasProduct)
            } else {
                Log.e("BillingManager", "Failed to query one-time purchases: ${billingResult.debugMessage}")
                onResult(false)
            }
        }
    }

    suspend fun isSubscriptionActive(productId: String): Boolean {
        return suspendCoroutine { continuation ->
            checkActiveSubscription(productId) { isActive ->
                continuation.resume(isActive)
            }
        }
    }

    suspend fun isOneTimeProductOwned(oneTimeProductId: String): Boolean {
        return suspendCoroutine { continuation ->
            checkPurchasedProduct(oneTimeProductId) { isPurchased ->
                continuation.resume(isPurchased)
            }
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}