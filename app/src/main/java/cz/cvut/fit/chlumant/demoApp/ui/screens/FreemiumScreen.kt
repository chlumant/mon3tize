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

@Composable
fun FreemiumScreen(navController: NavHostController) {
    val viewModel: FreemiumViewModel = viewModel()
    val isFreemiumActive by viewModel.isFreemiumActive.collectAsState()

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
                    text = "Chceš aktivovat zkušební dobu zdarma?",
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
                                navController.navigate("home") {
                                    popUpTo("freemium") { inclusive = true }
                                }
                            }
                        )
                    }
                ) {
                    Text("Ano, aktivovat")
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationButton(navController, "Ne, pokračovat bez freemia", "payment")
            } else {
                Text(
                    text = "Freemium je aktivní.",
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
                    Text("Zrušit freemium")
                }

                Spacer(modifier = Modifier.height(12.dp))
                NavigationButton(navController, "Zpět na domovskou obrazovku", "home")
            }
        }
    }
}