package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger
import cz.cvut.fit.chlumant.mon3tize.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class BillingManager(
    private val billingClient: BillingClient,
) : BillingActions {

    private val _isBillingReady = MutableStateFlow(false)

    init {
        Mon3tizeLogger.d("BillingManager", "BillingClient built, version 7.1.1")
    }

    internal suspend fun startConnection() {
        return suspendCancellableCoroutine { continuation ->
            try {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Mon3tizeLogger.d("BillingManager", "BillingClient is ready")
                            _isBillingReady.value = true
                            continuation.resume(Unit)
                        } else {
                            Mon3tizeLogger.e(
                                "BillingManager",
                                "Error setting up billing: ${billingResult.debugMessage}"
                            )
                            _isBillingReady.value = false
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        _isBillingReady.value = false
                    }
                })
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun getSubscriptionDetails(productId: String): ProductDetails {
        if (!_isBillingReady.value) {
            startConnection()
        }
        return suspendCancellableCoroutine { continuation ->
            try {
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
                        if (result != null) {
                            Mon3tizeLogger.d("BillingManager", "Subscription loaded: ${result.name}")
                            continuation.resume(result)
                        } else {
                            Mon3tizeLogger.e("BillingManager", "No matching subscription found for $productId")
                            error("No matching subscription found for $productId")
                        }
                    } else {
                        Mon3tizeLogger.e("BillingManager", "Query failed: ${billingResult.debugMessage}")
                        error("Query failed: ${billingResult.debugMessage}")
                    }
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun getOneTimeProductDetails(productId: String): ProductDetails {
        if (!_isBillingReady.value) {
            startConnection()
        }

        return suspendCancellableCoroutine { continuation ->
            try {
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
                        if (result != null) {
                            Mon3tizeLogger.d("BillingManager", "One-time product loaded: ${result.name}")
                            continuation.resume(result)
                        } else {
                            val msg = "No matching one-time product found for $productId"
                            Mon3tizeLogger.e("BillingManager", msg)
                            error("No matching product found for $productId")
                        }
                    } else {
                        Mon3tizeLogger.e("BillingManager", "Query failed: ${billingResult.debugMessage}")
                        error("Query failed: ${billingResult.debugMessage}")
                    }
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override fun launchInAppPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        try {
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

            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                val msg = "launchInAppPurchaseFlow failed: ${billingResult.responseCode} - ${billingResult.debugMessage}"
                Mon3tizeLogger.e("BillingManager", msg)
                throw IllegalStateException(msg)
            } else {
                Mon3tizeLogger.d("BillingManager", "launchInAppPurchaseFlow launched successfully.")
            }
        } catch (e: Exception) {
            Mon3tizeLogger.e("BillingManager", "launchInAppPurchaseFlow exception: ${e.localizedMessage}", e)
            throw e
        }
    }

    override fun launchSubscriptionPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        try {
            val offerToken = productDetails.subscriptionOfferDetails
                ?.firstOrNull()
                ?.offerToken
                ?: throw IllegalStateException("No offer token available for product ${productDetails.productId}")

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
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                val msg = "Subscription billing failed: ${billingResult.responseCode} - ${billingResult.debugMessage}"
                Mon3tizeLogger.e("BillingManager", msg)
                throw IllegalStateException(msg)
            } else {
                Mon3tizeLogger.d("BillingManager", "Subscription purchase launched successfully.")
            }
        } catch (e: Exception) {
            Mon3tizeLogger.e("BillingManager", "launchSubscriptionPurchaseFlow exception: ${e.localizedMessage}", e)
            throw e
        }
    }

    override suspend fun isSubscriptionActive(productId: String): Boolean {
        if (!_isBillingReady.value) {
            startConnection()
        }

        return suspendCancellableCoroutine { continuation ->
            try {
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
                        continuation.resume(hasActiveSubscription)
                    } else {
                        val msg = "Failed to query subscriptions: ${billingResult.debugMessage}"
                        Mon3tizeLogger.e("BillingManager", msg)
                        error(msg)
                    }
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun isOneTimeProductOwned(productId: String): Boolean {
        if (!_isBillingReady.value) {
            startConnection()
        }

        return suspendCancellableCoroutine { continuation ->
            try {
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
                        continuation.resume(hasProduct)
                    } else {
                        val msg = "Failed to query one-time purchases: ${billingResult.debugMessage}"
                        Mon3tizeLogger.e("BillingManager", msg)
                        error(msg)
                    }
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override fun openSubscriptionManagement(context: Context, productId: String): Result<Unit> {
        return try {
            val uri = "https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.android.vending")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Result.Success(Unit)
            } else {
                Mon3tizeLogger.e("BillingManager", "Google Play Store not available to handle subscription intent.")
                error("Nelze otevřít správu předplatného")
            }
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    override fun endConnection() {
        billingClient.endConnection()
    }
}