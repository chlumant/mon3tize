@file:Suppress("DEPRECATION")

package cz.cvut.fit.chlumant.demoApp.viewmodels

import android.app.Application
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    fun handleSignInResult(
        result: ActivityResult,
        onSuccess: (String) -> Unit,
        onError: (Exception?) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account: GoogleSignInAccount = task.getResult(Exception::class.java)!!
            val idToken = account.idToken
            if (idToken == null) {
                Log.e("SignIn", "ID token is null.")
                onError(IllegalStateException("ID token is null"))
                return
            }

            Mon3tize.freemium.auth.signInWithGoogleToken(idToken) { success, uid ->
                if (success && uid != null) {
                    Log.d("SignIn", "Sign In Successful: $uid")
                    onSuccess(uid)
                } else {
                    Log.e("SignIn", "Firebase Log In Failed")
                    onError(null)
                }
            }

        } catch (e: Exception) {
            Log.e("SignIn", "Google Log In Failed", e)
            onError(e)
        }
    }
}

