package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

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
            Text("Správa předplatného", fontSize = 22.sp)

            when (isActive) {
                null -> CircularProgressIndicator()
                true -> {
                    Text("Máte aktivní předplatné nebo freemium.")
                    Button(onClick = {
                        Mon3tize.billing.openSubscriptionManagement(context, "subscription_test_01") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {
                        Text("Zrušit / upravit předplatné")
                    }
                }
                false -> {
                    Text("Nemáte aktivní předplatné.")
                    NavigationButton(navController, "Zakoupit Předplatné", "payment")
                }
            }
            NavigationButton(navController, "Odhlásit se", "signout")
            NavigationButton(navController, "Zpět", "home")
        }
    }
}
