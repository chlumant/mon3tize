package cz.cvut.fit.chlumant.mon3tize.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Dialogs {
    @Composable
    fun Freemium(modifier: Modifier = Modifier) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Freemium Access") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Do you want to activate free trial?")
                }
            },
            confirmButton = {
                Button(onClick = { /* implement activate */ }) {
                    Text("Activate")
                }
            },
            dismissButton = {
                Button(onClick = { /* implement dismiss */ }) {
                    Text("Cancel")
                }
            },
            modifier = modifier.fillMaxWidth().padding(16.dp)
        )
    }

    @Composable
    fun Payment(modifier: Modifier = Modifier) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Payment Required") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Please subscribe or watch an ad to continue.")
                }
            },
            confirmButton = {
                Button(onClick = { /* implement payment */ }) {
                    Text("Subscribe")
                }
            },
            dismissButton = {
                Button(onClick = { /* implement dismiss */ }) {
                    Text("Cancel")
                }
            },
            modifier = modifier.fillMaxWidth().padding(16.dp)
        )
    }
}
