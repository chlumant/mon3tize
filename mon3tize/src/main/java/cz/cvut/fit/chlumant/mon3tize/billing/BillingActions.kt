package cz.cvut.fit.chlumant.mon3tize.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.ProductDetails
import cz.cvut.fit.chlumant.mon3tize.util.Result

public interface BillingActions {
    public suspend fun isSubscriptionActive(productId: String): Boolean

    public suspend fun isOneTimeProductOwned(productId: String): Boolean

    public suspend fun getSubscriptionDetails(productId: String): ProductDetails

    public suspend fun getOneTimeProductDetails(productId: String): ProductDetails

    public fun launchSubscriptionPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails
    )

    public fun launchInAppPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails
    )

    public fun openSubscriptionManagement(
        context: Context,
        productId: String
    ): Result<Unit>

    public fun endConnection()
}


