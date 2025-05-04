package cz.cvut.fit.chlumant.mon3tize.components


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
    fun TrialAlreadyUsedDialog(
        onDismiss: () -> Unit,
        onGoToSubscription: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Zkušební doba již byla vyčerpána")
            },
            text = {
                Text(text = "Již jste využili svou bezplatnou zkušební dobu." +
                        " Zakoupením předplatného získáte plný přístup.")
            },
            confirmButton = {
                Button(
                    onClick = onGoToSubscription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Zakoupit předplatné")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Zrušit")
                }
            }
        )
    }

    @Composable
    fun TrialExpiredDialog(
        onDismiss: () -> Unit,
        onGoToSubscription: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Zkušební doba skončila")
            },
            text = {
                Text("Vaše bezplatná zkušební doba vypršela. Pokud chcete nadále používat prémiové funkce, zakupte si předplatné.")
            },
            confirmButton = {
                Button(onClick = onGoToSubscription) {
                    Text("Zobrazit předplatné")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Zrušit")
                }
            }
        )
    }

}