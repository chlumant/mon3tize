package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys.AdMob.subscription_5_minutes
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreemiumViewModel : ViewModel() {

    private val _isFreemiumActive = MutableStateFlow(false)
    val isFreemiumActive: StateFlow<Boolean> = _isFreemiumActive

    init {
        viewModelScope.launch {
            val active = Mon3tize.freemium.isFreemiumCurrentlyActive()
            _isFreemiumActive.value = active
        }
    }

    private val _showTrialUsedDialog = MutableStateFlow(false)
    val showTrialUsedDialog: StateFlow<Boolean> = _showTrialUsedDialog

    fun startTrial(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit
    ) {
        viewModelScope.launch {
            Mon3tize.freemium.enableFreemium(
                onNeedSignIn = onNeedSignIn,
                onActivated = onActivated,
                onAlreadyUsed = {
                    _showTrialUsedDialog.value = true
                }
            )
        }
    }

//  TODO: tahle hardcoded vec se mi moc nelibi (ID)
    fun checkPremiumAccess(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = Mon3tize.isPremiumAccessAvailable(
                subscriptionProductId = subscription_5_minutes
            )
            onResult(result)
        }
    }

    fun refreshFreemiumStatus() {
        viewModelScope.launch {
            val active = Mon3tize.freemium.isFreemiumCurrentlyActive()
            _isFreemiumActive.value = active
        }
    }

    fun dismissTrialUsedDialog() {
        _showTrialUsedDialog.value = false
    }

    fun disableFreemium() {
        viewModelScope.launch {
            Mon3tize.freemium.disableFreemium()
        }
    }
}
