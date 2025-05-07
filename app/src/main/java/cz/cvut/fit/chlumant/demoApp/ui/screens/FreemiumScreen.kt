package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.viewmodels.FreemiumViewModel
import cz.cvut.fit.chlumant.mon3tize.components.Dialogs

@Composable
fun FreemiumScreen(navController: NavHostController) {
    val viewModel: FreemiumViewModel = viewModel()

    val isFreemiumActive by viewModel.isFreemiumActive.collectAsState()
    val showTrialUsedDialog by viewModel.showTrialUsedDialog.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!isFreemiumActive) {
                Text(
                    text = "Do you want to activate free trial?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        viewModel.startTrial(
                            onNeedSignIn = { navController.navigate("signin") },
                            onActivated = {
                                //TODO: prozkoumat
                                navController.navigate("freemiumYes") {
                                    popUpTo("freemium") { inclusive = true }
                                }
                            }
                        )
                    }
                ) {
                    Text("Activate")
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationButton(navController, "Dismiss", "home")
            } else {
                Text(
                    text = "Free trial already active.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        viewModel.disableFreemium()
                        navController.navigate("home") {
                            popUpTo("freemium") { inclusive = true }
                        }
                    }
                ) {
                    Text("Cancel Free Trial")
                }

                Spacer(modifier = Modifier.height(12.dp))
                NavigationButton(navController, "Back To Home Screen", "home")
            }
        }
    }

    if (showTrialUsedDialog) {
        Dialogs.TrialAlreadyUsedDialog(
            onDismiss = { viewModel.dismissTrialUsedDialog() },
            onGoToSubscription = {
                viewModel.dismissTrialUsedDialog()
                navController.navigate("payment")
            }
        )
    }
}
