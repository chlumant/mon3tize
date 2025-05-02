package cz.cvut.fit.chlumant.demoApp.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

class PaymentViewModel : ViewModel() {

    private val billingManager = Mon3tize.billingManager

    val isBillingReady = billingManager.isBillingReady

    private val _subscriptionProductDetails = MutableStateFlow<ProductDetails?>(null)
    val subscriptionProductDetails: StateFlow<ProductDetails?> = _subscriptionProductDetails

    private val _oneTimeProductDetails = MutableStateFlow<ProductDetails?>(null)
    val oneTimeProductDetails: StateFlow<ProductDetails?> = _oneTimeProductDetails

    init {
        viewModelScope.launch {
            billingManager.startConnection {
                loadProducts()
            }
        }
    }

    private fun loadProducts() {
        billingManager.querySubscriptions("subscription_test_01") { details ->
            _subscriptionProductDetails.value = details
        }

        billingManager.queryOneTimeProduct("remove_ads_test_01") { details ->
            _oneTimeProductDetails.value = details
        }
    }

    fun buySubscription(activity: Activity) {
        subscriptionProductDetails.value?.let {
            billingManager.launchSubscriptionPurchaseFlow(activity, it)
        }
    }

    fun buyOneTimeProduct(activity: Activity) {
        oneTimeProductDetails.value?.let {
            billingManager.launchInAppPurchaseFlow(activity, it)
        }
    }
}
