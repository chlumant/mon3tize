package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.billingclient.api.*
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.viewmodels.PaymentViewModel
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

@Composable
fun PaymentScreen(navController: NavHostController) {
    val activity = LocalActivity.current

    val viewModel: PaymentViewModel = viewModel()

    val screenState by viewModel.screenStateStream.collectAsState()
//
//    val rewardedAdManager = remember {
//        RewardedAdManager(activity, UserKeys.AdMob.REWARDED_DEMO)
//    }
//
//    LaunchedEffect(Unit) {
//        rewardedAdManager.loadAd()
//    }

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
//                    rewardedAdManager.showAd {
//                        Log.d("PaymentScreen", "User earned the reward!")
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Watch Ad to Earn Reward")
            }

            when (val currentScreenState = screenState) {
                is PaymentViewModel.ScreenState.Error -> TODO()
                is PaymentViewModel.ScreenState.Loaded -> {
                    Button(
                        onClick = {
                            activity?.let { activity ->
                                Mon3tize.billing.launchSubscriptionPurchaseFlow(
                                    activity,
                                    currentScreenState.subscription
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Koupit Předplatné")
                    }

                    Button(
                        onClick = {
                            activity?.let { activity ->
                                Mon3tize.billing.launchInAppPurchaseFlow(activity, currentScreenState.oneTimeProduct)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Jednorázový nákup")
                    }
                }

                is PaymentViewModel.ScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            NavigationButton(navController, "Zpět na Home", "home")
        }
    }
}
