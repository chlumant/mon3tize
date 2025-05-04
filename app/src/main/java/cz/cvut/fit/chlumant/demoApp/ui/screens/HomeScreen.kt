package cz.cvut.fit.chlumant.demoApp.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdSize
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.components.AdBanner
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {

    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        Mon3tize.ads.preloadInterstitial(
            UserKeys.AdMob.INTERSTITIAL_DEMO,
            onError = {
                showToast(activity, "Error while preloading add")
            }
        )
    }

    LaunchedEffect(Unit) {
        val user = Firebase.auth.currentUser
        val isFreemium = Mon3tize.freemium.isFreemiumCurrentlyActive()
        Log.d(
            "AUTH",
            "Uživatel: ${user?.email ?: "anonymní"} | Freemium aktivní: $isFreemium"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AdBanner(adUnitId = UserKeys.AdMob.BANNER_DEMO, adSize = AdSize.FULL_BANNER)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            NavigationButton(navController, "Go to Main Screen", "main")
            NavigationButton(navController, "Go to Freemium Screen", "freemium")

            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    activity?.let { activity ->
                        coroutineScope.launch {
                            Mon3tize.ads.showInterstitial(
                                activity = activity,
                                adUnitId = UserKeys.AdMob.INTERSTITIAL_DEMO,
                                onClose = { navController.navigate("freemium") },
                                onError = {
                                    showToast(activity, "Error while showing ad")
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Go to Freemium Screen but with Ad")
            }
            NavigationButton(navController, "Resetovat Zkušební Dobu", "resettrial")
            NavigationButton(navController, "Nastavení Předplatného", "subscriptionsettings")
        }
    }
}

private fun showToast(activity: Activity?, message: String) {
    Toast.makeText(
        activity,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

