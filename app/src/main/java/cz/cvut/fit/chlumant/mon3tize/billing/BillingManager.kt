package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class BillingManager(
    context: Context,
    listener: PurchasesUpdatedListener
) {

    private val _isBillingReady = MutableStateFlow(false)

    init {
        Log.d("BillingManager", "BillingClient built, version 7.1.1")
    }

    @Suppress("DEPRECATION")
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(listener)
        .enablePendingPurchases()
        .build()

    private suspend fun startConnection() {
        return suspendCancellableCoroutine { continuation ->
            try {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d("BillingManager", "BillingClient is ready")
                            _isBillingReady.value = true
                            continuation.resume(Unit)
                        } else {
                            Log.e(
                                "BillingManager",
                                "Error setting up billing: ${billingResult.debugMessage}"
                            )
                            _isBillingReady.value = false
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Log.w("BillingManager", "Billing service disconnected")
                        _isBillingReady.value = false
                    }
                })
            } catch (e: Throwable){
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun getSubscriptionDetails(productId: String): ProductDetails {
        if(!_isBillingReady.value){
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
                            continuation.resume(result)
                        } else {
                            Log.e("BillingManager", "No matching product found for $productId")
                            error("No matching product found for $productId")
                        }
                    } else {
                        Log.e("BillingManager", "Query failed: ${billingResult.debugMessage}")
                        error("Query failed: ${billingResult.debugMessage}")
                    }
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun getOneTimeProductDetails(productId: String):ProductDetails {
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
        Log.e(
            "BillingManager",
            "Query failed: ${billingResult.responseCode} - ${billingResult.debugMessage}"
        )
    }

    fun launchSubscriptionPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: run {
            Log.e(
                "BillingManager",
                "No offer token available for product ${productDetails.productId}"
            )
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

    private fun checkActiveSubscription(productId: String, onResult: (Boolean) -> Unit) {
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
                Log.e(
                    "BillingManager",
                    "Failed to query subscriptions: ${billingResult.debugMessage}"
                )
                onResult(false)
            }
        }
    }

    private fun checkPurchasedProduct(productId: String, onResult: (Boolean) -> Unit) {
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
                Log.e(
                    "BillingManager",
                    "Failed to query one-time purchases: ${billingResult.debugMessage}"
                )
                onResult(false)
            }
        }
    }

    fun openSubscriptionManagement(context: Context, productId: String) {
        val uri =
            "https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}"
                .toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.android.vending")
        context.startActivity(intent)
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

    //  dolu debug
    fun logActiveSubscriptions() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isEmpty()) {
                    Log.d("BillingDebug", "Žádná aktivní předplatná nenalezena.")
                } else {
                    purchasesList.forEach { purchase ->
                        Log.d(
                            "BillingDebug",
                            "Aktivní předplatné: ${purchase.products}, acknowledged: ${purchase.isAcknowledged}"
                        )
                    }
                }
            } else {
                Log.e(
                    "BillingDebug",
                    "Chyba při načítání předplatných: ${billingResult.debugMessage}"
                )
            }
        }
    }

    fun logAllActiveSubscriptions() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isEmpty()) {
                    Log.d("BillingDebug", "Žádné předplatné nebylo nalezeno.")
                } else {
                    purchasesList.forEach { purchase ->
                        val productIdList = purchase.products.joinToString()
                        val state = when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> "PURCHASED"
                            Purchase.PurchaseState.PENDING -> "PENDING"
                            else -> "UNSPECIFIED"
                        }
                        val ack = if (purchase.isAcknowledged) "YES" else "NO"
                        Log.d(
                            "BillingDebug",
                            "Produkt: [$productIdList], Stav: $state, Acknowledged: $ack"
                        )
                    }
                }
            } else {
                Log.e(
                    "BillingDebug",
                    "Chyba při dotazu na předplatné: ${billingResult.debugMessage}"
                )
            }
        }
    }


    fun endConnection() {
        billingClient.endConnection()
    }
}