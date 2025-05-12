package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys.Billing.SUBSCRIPTION_PRODUCT_ID
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreemiumViewModel : ViewModel() {

    private val _isFreemiumActive = MutableStateFlow(false)
    val isFreemiumActive: StateFlow<Boolean> = _isFreemiumActive

    private val _trialExpired = MutableStateFlow(false)
    val trialExpired: StateFlow<Boolean> = _trialExpired
    private var trialExpiredShown = false

    private val _showTrialUsedDialog = MutableStateFlow(false)
    val showTrialUsedDialog: StateFlow<Boolean> = _showTrialUsedDialog


    init {
        syncFreemiumStatus()
    }

    private fun syncFreemiumStatus() {
        viewModelScope.launch {
            val active = Mon3tize.freemium.isFreemiumCurrentlyActive()
            _isFreemiumActive.value = active
        }
    }

    fun startTrial(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit
    ) {
        viewModelScope.launch {
            Mon3tize.freemium.enableFreemium(
                onNeedSignIn = onNeedSignIn,
                onActivated = {
                    syncFreemiumStatus()
                    onActivated()
                },
                onAlreadyUsed = {
                    _showTrialUsedDialog.value = true
                }
            )
        }
    }

    fun checkPremiumAccess(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = Mon3tize.isPremiumAccessAvailable(
                subscriptionProductId = SUBSCRIPTION_PRODUCT_ID
            )
            onResult(result)
        }
    }

    fun checkTrialStatus(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = Mon3tize.freemium.isFreemiumCurrentlyActive()
            onResult(result)
        }
    }

    fun checkSubscriptionStatus(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = Mon3tize.billing.isSubscriptionActive(SUBSCRIPTION_PRODUCT_ID)
            onResult(result)
        }
    }

//    todo tohle se mi nejak nezda
    fun dismissTrialUsedDialog() {
        _showTrialUsedDialog.value = false
    }

    //    todo tohle se mi nejak nezda
    fun hideTrialDialog() {
        _trialExpired.value = false
    }

    fun disableFreemium() {
        viewModelScope.launch {
            Mon3tize.freemium.disableFreemium()
        }
    }

    //TODO: nemel bych pro uplynuti predplatnyho mit nejakej podobnej booelan jako trialUsed
    //TODO: !isActive && (info?.trialUsed == true || subscriptionExpired) && !trialExpiredShown
    fun checkPremium() {
        viewModelScope.launch {
            val isActive = Mon3tize.isPremiumAccessAvailable(SUBSCRIPTION_PRODUCT_ID)
            val trialUsed = Mon3tize.freemium.getTrialStatus()

            val shouldShowDialog = !isActive && trialUsed == true && !trialExpiredShown

            if (shouldShowDialog) {
                _trialExpired.value = true
                trialExpiredShown = true
            }
        }
    }
}
