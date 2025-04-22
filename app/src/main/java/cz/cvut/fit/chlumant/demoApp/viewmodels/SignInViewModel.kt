package cz.cvut.fit.chlumant.demoApp.viewmodels

import android.app.Application
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import cz.cvut.fit.chlumant.mon3tize.AuthManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

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
                Log.e("SignIn", "ID token je null.")
                onError(IllegalStateException("ID token is null"))
                return
            }

            AuthManager.signInWithGoogleToken(idToken) { success, uid ->
                if (success && uid != null) {
                    Log.d("SignIn", "Přihlášení úspěšné: $uid")
                    onSuccess(uid)
                } else {
                    Log.e("SignIn", "Firebase přihlášení selhalo")
                    onError(null)
                }
            }

        } catch (e: Exception) {
            Log.e("SignIn", "Google přihlášení selhalo", e)
            onError(e)
        }
    }
}

