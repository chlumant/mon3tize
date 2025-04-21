package cz.cvut.fit.chlumant.demoApp.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun handleSignInResult(result: ActivityResult, onSuccess: (String) -> Unit, onError: (Exception?) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(Exception::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = auth.currentUser
                        Log.d("SignIn", "Přihlášení úspěšné: ${user?.email}")
                        onSuccess(user?.email ?: "")
                    } else {
                        Log.e("SignIn", "Firebase přihlášení selhalo", authResult.exception)
                        onError(authResult.exception)
                    }
                }
        } catch (e: Exception) {
            Log.e("SignIn", "Google přihlášení selhalo", e)
            onError(e)
        }
    }
}
