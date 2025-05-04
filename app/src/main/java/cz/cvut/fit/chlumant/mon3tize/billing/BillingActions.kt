package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.ProductDetails

interface BillingActions {
    suspend fun isSubscriptionActive(productId: String): Boolean

    suspend fun isOneTimeProductOwned(productId: String): Boolean

    suspend fun getSubscriptionDetails(productId: String): ProductDetails

    suspend fun getOneTimeProductDetails(productId: String): ProductDetails

    fun launchSubscriptionPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails
    )

    fun launchInAppPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails
    )

    fun openSubscriptionManagement(
        context: Context,
        productId: String
    )

//    TODO: debug veci - optional pridat pozdejs
//    fun logActiveSubscriptions()
//    fun logAllActiveSubscriptions()
}


