package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.demoApp.ui.components.showToast
import cz.cvut.fit.chlumant.demoApp.viewmodels.PaymentViewModel
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    navController: NavHostController,
    signInViewModel: SignInViewModel = viewModel()
) {

    val activity = LocalActivity.current
    val viewModel: PaymentViewModel = viewModel()
    val screenState by viewModel.screenStateStream.collectAsState()

    LaunchedEffect(Unit) {
        Mon3tize.ads.preloadRewarded(
            adUnitId = UserKeys.AdMob.REWARDED_DEMO,
            onError = {
                showToast(activity, "Error while preloading add")
            })
    }

    val isUserSignedIn = signInViewModel.isUserSignedIn()

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
                text = "Select Product",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    activity?.let { activity ->
                        coroutineScope.launch {
                            if(isUserSignedIn) {
                                Mon3tize.ads.showRewarded(
                                    activity = activity,
                                    adUnitId = UserKeys.AdMob.REWARDED_DEMO,
                                    onRewardEarn = { reward -> viewModel.handleReward(reward) },
                                    onClose = {},
                                    onError = { showToast(activity, "Error while showing ad") }
                                )
                            } else {
                                showToast(activity, "Sign in to receive the trial.")
                                navController.navigate("signin")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Watch Ad to Earn 1 Day Free Trial")
            }

            when (val currentScreenState = screenState) {
                is PaymentViewModel.ScreenState.Error -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Error while loading data")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = viewModel::loadProducts) {
                            Text("Reload")
                        }
                    }
                }
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
                        Text("Purchase Subscription")
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
                        Text("One-Time Purchase")
                    }
                }

                is PaymentViewModel.ScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            NavigationButton(navController, "Back To Home Screen", "home")
        }
    }
}
