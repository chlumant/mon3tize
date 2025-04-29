package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FreemiumViewModel : ViewModel() {

    private val manager = Mon3tize.freemiumManager

    val isFreemiumActive = manager.isFreemiumActive
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _showTrialUsedDialog = MutableStateFlow(false)
    val showTrialUsedDialog: StateFlow<Boolean> = _showTrialUsedDialog

    fun startTrial(
        onNeedSignIn: () -> Unit,
        onActivated: () -> Unit
    ) {
        viewModelScope.launch {
            manager.enableFreemium(
                onNeedSignIn = onNeedSignIn,
                onActivated = onActivated,
                onAlreadyUsed = {
                    _showTrialUsedDialog.value = true
                }
            )
        }
    }

    fun dismissTrialUsedDialog() {
        _showTrialUsedDialog.value = false
    }

    fun disableFreemium() {
        viewModelScope.launch {
            manager.disableFreemium()
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            manager.synchronizeWithFirebase()
        }
    }
}
