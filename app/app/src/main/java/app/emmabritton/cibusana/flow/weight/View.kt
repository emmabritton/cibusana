package app.emmabritton.cibusana.flow.weight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.cibusana.ui.theme.Dimen.Padding
import app.emmabritton.system.ActionReceiver
import java.time.format.DateTimeFormatter

@Composable
fun WeightUi(weightState: WeightState, actionReceiver: ActionReceiver) {
    when (weightState) {
        is WeightState.Viewing -> ViewingUi(weightState = weightState, actionReceiver = actionReceiver)
        is WeightState.Loading -> LoadingUi()
        WeightState.Error -> ErrorUi(actionReceiver = actionReceiver)
    }
}

@Composable
private fun ViewingUi(weightState: WeightState.Viewing, actionReceiver: ActionReceiver) {
    Column(Modifier.fillMaxSize()) {
        Text("Start: ${weightState.range.start.format(DateTimeFormatter.ISO_DATE)}")
        Text("End: ${weightState.range.end.format(DateTimeFormatter.ISO_DATE)}")
        LazyColumn(Modifier.fillMaxSize()) {
            items(weightState.weights.toList()) {
                Text("${it.first} ${it.second}kg", modifier = Modifier.padding(Padding.Small))
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
        Button({ actionReceiver.receive(WeightAction.Show) }) {
            Text("OK")
        }
    }
}