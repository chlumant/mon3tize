package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.random.Random
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.fit.chlumant.demoApp.viewmodels.FreemiumViewModel


import cz.cvut.fit.chlumant.mon3tize.Mon3tize


@Composable
fun MainScreen(navController: NavHostController) {
    RockPaperScissorsGame(navController)
}

@Composable
fun RockPaperScissorsGame(navController: NavHostController) {
    val viewModel: FreemiumViewModel = viewModel()
    var hasPremiumAccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPremiumAccess { result ->
            hasPremiumAccess = result
        }
    }

    var playerChoice by remember { mutableStateOf<String?>(null) }
    var computerChoice by remember { mutableStateOf<String?>(null) }
    var result by remember { mutableStateOf<String?>(null) }

    val choices = listOf("Kámen", "Nůžky", "Papír")

    fun playGame(choice: String) {
        playerChoice = choice
        computerChoice = choices[Random.nextInt(choices.size)]

        result = when {
            playerChoice == computerChoice -> "Remíza!"
            (playerChoice == "Kámen" && computerChoice == "Nůžky") ||
                    (playerChoice == "Nůžky" && computerChoice == "Papír") ||
                    (playerChoice == "Papír" && computerChoice == "Kámen") -> "Vyhrál jsi!"
            else -> "Prohrál jsi!"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Vyber si tah", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            choices.forEach { choice ->
                val isEnabled = choice != "Papír" || hasPremiumAccess

                Button(
                    onClick = { playGame(choice) },
                    enabled = isEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (!isEnabled) "$choice 🔒" else choice,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (!hasPremiumAccess) {
            Text(
                text = "Pro výběr 'Papír' je potřeba aktivovat zkušební dobu nebo mít prémiový účet.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (playerChoice != null && computerChoice != null) {
            Text(text = "Tvoje volba: $playerChoice", fontSize = 20.sp)
            Text(text = "Počítač zvolil: $computerChoice", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = result ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when (result) {
                    "Vyhrál jsi!" -> Color.Green
                    "Prohrál jsi!" -> Color.Red
                    else -> Color.Gray
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        NavigationButton(navController, "Zpět na domovskou obrazovku", "home")
    }
}
