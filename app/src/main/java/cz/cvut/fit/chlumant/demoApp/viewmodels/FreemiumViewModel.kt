package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FreemiumViewModel : ViewModel() {

    private val manager = Mon3tize.freemiumManager

    val isFreemiumActive = manager.isFreemiumEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isFirstLaunch = manager.isFirstLaunch
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun startTrial(onNeedSignIn: () -> Unit, onActivated: () -> Unit) {
        viewModelScope.launch {
            manager.enableFreemium(
                onNeedSignIn = onNeedSignIn,
                onActivated = onActivated
            )
        }
    }

    fun disableFreemium() {
        viewModelScope.launch {
            manager.disableFreemium()
        }
    }

    fun setFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            manager.setFirstLaunch(value)
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            manager.synchronizeWithFirebase()
        }
    }
}
