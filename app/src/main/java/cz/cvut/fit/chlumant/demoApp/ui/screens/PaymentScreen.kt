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
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.demoApp.viewmodels.PaymentViewModel
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(navController: NavHostController) {

    val activity = LocalActivity.current
    val viewModel: PaymentViewModel = viewModel()
    val screenState by viewModel.screenStateStream.collectAsState()

    LaunchedEffect(Unit) {
        Mon3tize.ads.preloadRewarded(
            adUnitId = UserKeys.AdMob.REWARDED_DEMO,
            onError = {
                Mon3tize.ads.showToast(activity, "Error while preloading add")
        })
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

            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    activity?.let { activity ->
                        coroutineScope.launch {
                            Mon3tize.ads.showRewarded(
                                activity = activity,
                                adUnitId = UserKeys.AdMob.REWARDED_DEMO,
                                //TODO: co s tim on rewarded?
                                onRewarded = {},
                                onClose = {},
                                onError = {
                                    Mon3tize.ads.showToast(activity, "Error while showing ad")
                            })
                        }
                    }
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
