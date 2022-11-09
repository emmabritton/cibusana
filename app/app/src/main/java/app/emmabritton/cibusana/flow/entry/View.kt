package app.emmabritton.cibusana.flow.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.emmabritton.system.ActionReceiver
import java.time.format.DateTimeFormatter

@Composable
fun EntryUi(state: EntryState, actionReceiver: ActionReceiver) {
    when (state) {
        is EntryState.Error -> ErrorUi(actionReceiver)
        is EntryState.Loading -> LoadingUi()
        is EntryState.Viewing -> ViewingUi(state, actionReceiver)
    }
}

@Composable
private fun ViewingUi(entryState: EntryState.Viewing, actionReceiver: ActionReceiver) {
    Column(Modifier.fillMaxSize()) {
        Text("For: ${entryState.date.format(DateTimeFormatter.ISO_DATE)}")
        for (mealTime in entryState.entries.keys) {
           Text(mealTime)
           Column {
               for (item in entryState.entries[mealTime] ?: emptyList()) {
                   val food = entryState.food[item.foodId]
                   Text("${food?.name} ${item.grams}g ${item.calories}kcal")
               }
           }
        }
    }
}

@Composable
private fun LoadingUi() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorUi(actionReceiver: ActionReceiver) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error")
        Button({ actionReceiver.receive(EntryAction.Show) }) {
            Text("OK")
        }
    }
}