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
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlinx.coroutines.launch

@Composable
fun FreemiumScreen(navController: NavHostController) {
    val viewModel: FreemiumViewModel = viewModel()

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
                text = "Do you wish to activate free trial?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    viewModel.startTrial(
                        onNeedSignIn = { navController.navigate("sign_in") },
                        onActivated = {
                            navController.navigate("home") {
                                popUpTo("freemium") { inclusive = true }
                            }
                        }
                    )
                }
            ) {
                Text("Yes")
            }

            NavigationButton(navController, "No", "payment")
        }
    }
}
