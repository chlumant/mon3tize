@file:Suppress("DEPRECATION")

package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface FirebaseAuthActions {

    fun signInWithGoogleToken(idToken: String, onResult: (Boolean, String?) -> Unit)

    fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient

    // TODO - Přidat další funkce
}