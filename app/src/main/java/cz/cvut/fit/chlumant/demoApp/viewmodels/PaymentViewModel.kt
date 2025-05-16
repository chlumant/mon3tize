package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.ads.AdReward
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

class PaymentViewModel : ViewModel() {

    private val _screenStateStream = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenStateStream = _screenStateStream.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _screenStateStream.value = ScreenState.Loading
                coroutineScope {
                    val subscriptionProductDetail = async {
                        Mon3tize.billing.getSubscriptionDetails(UserKeys.Billing.SUBSCRIPTION_PRODUCT_ID)
                    }
                    val oneTimeProductDetail = async {
                        Mon3tize.billing.getOneTimeProductDetails(UserKeys.Billing.ONE_TIME_PRODUCT_ID)
                    }
                    _screenStateStream.value = ScreenState.Loaded(
                        subscription = subscriptionProductDetail.await(),
                        oneTimeProduct = oneTimeProductDetail.await(),
                    )
                }
            } catch (e: Throwable) {
                _screenStateStream.value = ScreenState.Error(e)
            }
        }
    }

    fun handleReward(reward: AdReward) {
        viewModelScope.launch {
            try {
                if (reward.type == UserKeys.Freemium.REWARD_TYPE) {
                    Mon3tize.freemium.extendFreemiumBy(1.days)
                }
            } catch (e: Exception) {
                _screenStateStream.update { it.asLoaded()?.copy(rewardError = e) ?: it }
            }
        }
    }

    sealed interface ScreenState {

        fun asLoaded() = this as? Loaded

        data object Loading : ScreenState

        data class Error(val error: Throwable) : ScreenState

        data class Loaded(
            val subscription: ProductDetails,
            val oneTimeProduct: ProductDetails,
            val rewardError: Throwable? = null,
        ) : ScreenState
    }
}
