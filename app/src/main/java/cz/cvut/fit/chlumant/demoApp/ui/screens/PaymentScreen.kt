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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.fit.chlumant.mon3tize.adManagers.RewardedAdManager
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import com.android.billingclient.api.*
import cz.cvut.fit.chlumant.demoApp.viewmodels.PaymentViewModel

@Composable
fun PaymentScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as Activity

    val viewModel: PaymentViewModel = viewModel()

    val isBillingReady by viewModel.isBillingReady.collectAsState()
    val subscriptionProductDetails by viewModel.subscriptionProductDetails.collectAsState()
    val oneTimeProductDetails by viewModel.oneTimeProductDetails.collectAsState()

    val rewardedAdManager = remember {
        RewardedAdManager(activity, UserKeys.AdMob.REWARDED_DEMO)
    }

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
                text = "Vyberte, co chcete zakoupit",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = {
                    rewardedAdManager.showAd {
                        Log.d("PaymentScreen", "User earned the reward!")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Watch Ad to Earn Reward")
            }

            if (isBillingReady) {
                Button(
                    onClick = { viewModel.buySubscription(activity) },
                    enabled = subscriptionProductDetails != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Koupit Předplatné")
                }

                Button(
                    onClick = { viewModel.buyOneTimeProduct(activity) },
                    enabled = oneTimeProductDetails != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Jednorázový nákup")
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            NavigationButton(navController, "Zpět na Home", "home")
        }
    }
}
