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
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.mon3tize.billing.BillingManager
import com.android.billingclient.api.*

@Composable
fun PaymentScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as Activity

    val rewardedAdManager = remember {
        RewardedAdManager(activity, UserKeys.AdMob.REWARDED_DEMO)
    }

    val billingManager = remember {
        BillingManager(context, PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Log.d("Billing", "Subscription purchase successful: ${purchases.first().products}")
                // TODO: Aktivuj freemium např. Mon3tize.enableFreemium()
            } else {
                Log.w("Billing", "Subscription failed: ${billingResult.debugMessage}")
            }
        })
    }

    var productDetails by remember { mutableStateOf<ProductDetails?>(null) }

    LaunchedEffect(Unit) {
        try {
            rewardedAdManager.loadAd()

            billingManager.startConnection {
                billingManager.querySubscriptions("subscription_test_01") { details ->
                    if (details != null) {
                        Log.d("PaymentScreen", "Subscription loaded: ${details.name}")
                        productDetails = details
                    } else {
                        Log.e("PaymentScreen", "Subscription productDetails was null")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PaymentScreen", "Exception during billing setup: ${e.localizedMessage}", e)
        }
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
                text = "Zaplat, zmrde, test",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(
                onClick = {
                    rewardedAdManager.showAd {
                        Log.d("PaymentScreen", "User earned the reward!")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Watch Ad to Earn Reward")
            }

            Button(
                onClick = {
                    productDetails?.let {
                        billingManager.launchPurchaseFlow(activity, it)
                    } ?: run {
                        Log.e("PaymentScreen", "Subscription productDetails not loaded")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Buy Subscription")
            }

            NavigationButton(navController, "Zpět na Home", "home")
        }
    }
}
