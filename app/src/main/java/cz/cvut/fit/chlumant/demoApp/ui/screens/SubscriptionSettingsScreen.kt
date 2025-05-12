package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys.Billing.SUBSCRIPTION_PRODUCT_ID
import cz.cvut.fit.chlumant.demoApp.viewmodels.FreemiumViewModel
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

@Composable
fun SubscriptionSettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: FreemiumViewModel = viewModel()

    var isActive by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        viewModel.checkPremiumAccess { result ->
            isActive = result
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Subscription Management", fontSize = 22.sp)

            when (isActive) {
                null -> CircularProgressIndicator()
                true -> {
                    Text("You have an active subscription or a free trial.")
                    Button(
                        onClick = {
                            Mon3tize.billing.openSubscriptionManagement(context, SUBSCRIPTION_PRODUCT_ID)
                            navController.navigate("home")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Cancel Subscription")
                    }
                    Button(
                        onClick = {
                            viewModel.disableFreemium()
                            navController.navigate("home")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Cancel Free Trial")
                    }
                }
                false -> {
                    Text("You have no active subscriptions.")
                    NavigationButton(navController, "Buy Subscription", "payment")
                }
            }
            NavigationButton(navController, "Sign Out", "signout")
            NavigationButton(navController, "Back To Home Screen", "home")
        }
    }
}
