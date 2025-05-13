package cz.cvut.fit.chlumant.demoApp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    fun handleSignIn(
        context: Application,
        onSuccess: (String) -> Unit,
        onError: (Exception?) -> Unit
    ) {
        Log.d("SignInViewModel", "handleSignIn called.")
        viewModelScope.launch {
            Log.d("SignInViewModel", "Coroutine started in handleSignIn.")
            try {
                Mon3tize.freemium.auth.signInWithGoogle(context) { success, uid ->
                    if (success && uid != null) {
                        Log.d("SignInViewModel", "Sign In Successful: $uid")
                        onSuccess(uid)
                    } else {
                        Log.e("SignInViewModel", "Sign In Failed - success: $success, uid: $uid")
                        onError(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Exception in sign-in process: ${e.message}")
                onError(e)
            }
        }
    }

    fun signOut() {
        Mon3tize.freemium.auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return Mon3tize.freemium.auth.isUserSignedIn()
    }
}