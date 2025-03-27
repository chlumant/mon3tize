package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.cvut.fit.chlumant.mon3tize.adManagers.RewardedAdManager
//import cz.cvut.fit.chlumant.demoApp.data.BillingManager
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton

@Composable
fun PaymentScreen(navController: NavHostController) {
    val context = LocalContext.current
    val rewardedAdManager = remember { RewardedAdManager(context as Activity, "ca-app-pub-3940256099942544/5224354917") }

    LaunchedEffect(Unit) {
        rewardedAdManager.loadAd()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Zaplat, zmrde",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Button(
                onClick = {
                    rewardedAdManager.showAd {
                        //tady si uzivatel vlozi svoje vlastni zpracovani odmeny - v AdMobu si
                        // kde si tu logiku napise?
                        //https://developers.google.com/admob/android/ssv#ssv_callback_parameters - je potreba kontrolovat?
                        Log.d("PaymentScreen", "User earned the reward!")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Watch Ad to Earn Reward")
            }
            NavigationButton(navController, "zpatky home", "home")
        }
    }
}

