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
import cz.cvut.fit.chlumant.mon3tize.components.Dialogs


@Composable
fun MainScreen(navController: NavHostController) {
    RockPaperScissorsGame(navController)
}

@Composable
fun RockPaperScissorsGame(navController: NavHostController) {
    val viewModel: FreemiumViewModel = viewModel()
    var hasPremiumAccess by remember { mutableStateOf(false) }
    val trialExpired by viewModel.trialExpired.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPremiumAccess { result ->
            hasPremiumAccess = result
        }
        viewModel.checkPremium()
    }

    if (trialExpired) {
        Dialogs.TrialExpiredDialog(
            onDismiss = {
                viewModel.hideTrialDialog()
            },
            onGoToSubscription = {
                navController.navigate("payment")
                viewModel.hideTrialDialog()
            }
        )
    }

    var playerChoice by remember { mutableStateOf<String?>(null) }
    var computerChoice by remember { mutableStateOf<String?>(null) }
    var result by remember { mutableStateOf<String?>(null) }

    val choices = listOf("Rock", "Scissors", "Paper")

    fun playGame(choice: String) {
        playerChoice = choice
        computerChoice = choices[Random.nextInt(choices.size)]

        result = when {
            playerChoice == computerChoice -> "Draw!"
            (playerChoice == "Rock" && computerChoice == "Scissors") ||
                    (playerChoice == "Scissors" && computerChoice == "Paper") ||
                    (playerChoice == "Paper" && computerChoice == "Rock") -> "You Won!"
            else -> "You Lost!"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Make Your Choice:", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            choices.forEach { choice ->
                val isEnabled = choice != "Paper" || hasPremiumAccess

                Button(
                    onClick = { playGame(choice) },
                    enabled = isEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
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
                text = "'Paper' is only available for premium users.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (playerChoice != null && computerChoice != null) {
            Text(text = "Your Choice: $playerChoice", fontSize = 20.sp)
            Text(text = "Computer's Choice: $computerChoice", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = result ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when (result) {
                    "You Won!" -> Color.Green
                    "You Lost!" -> Color.Red
                    else -> Color.Gray
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        NavigationButton(navController, "Back To Home Screen", "home")
    }
}
