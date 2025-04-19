package cz.cvut.fit.chlumant.demoApp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FreemiumViewModel : ViewModel() {

    val isFreemiumActive = Mon3tize.freemiumManager.isFreemiumEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isFirstLaunch = Mon3tize.freemiumManager.isFirstLaunch
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun startTrial() {
        viewModelScope.launch {
            Mon3tize.freemiumManager.enableFreemium()
        }
    }

    fun disableFreemium() {
        viewModelScope.launch {
            Mon3tize.freemiumManager.disableFreemium()
        }
    }

    fun setFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            Mon3tize.freemiumManager.setFirstLaunch(value)
        }
    }
}