package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys.AdMob.subscription_5_minutes
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {


    private val _screenStateStream = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenStateStream = _screenStateStream.asStateFlow()

    init {
        viewModelScope.launch {
            loadProducts()
        }
    }

    private suspend fun loadProducts() {
        try {
            _screenStateStream.value = ScreenState.Loading
            coroutineScope {
                val subscriptionProductDetail = async {
                    Mon3tize.billing.getSubscriptionDetails(subscription_5_minutes)
                }
                val oneTimeProductDetail = async {
                    Mon3tize.billing.getOneTimeProductDetails("remove_ads_test_01")
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

//    TODO: kdyz je to takhle implementovany, tak muzu mit ve viewmodelu max jeden One-time a jeden Subscription?
//    v tom loaded mit nejaky listy nebo tak neco? idk
    sealed interface ScreenState {

        data object Loading : ScreenState

        data class Error(val error: Throwable) : ScreenState

        data class Loaded(
            val subscription: ProductDetails,
            val oneTimeProduct: ProductDetails,
        ) : ScreenState
    }
}
