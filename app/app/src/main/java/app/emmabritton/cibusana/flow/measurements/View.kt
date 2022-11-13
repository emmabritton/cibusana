package app.emmabritton.cibusana.flow.measurements

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
fun MeasurementUi(state: MeasurementState, actionReceiver: ActionReceiver) {
    when (state) {
        is MeasurementState.Error -> ErrorUi(actionReceiver)
        is MeasurementState.Loading -> LoadingUi()
        is MeasurementState.Viewing -> ViewingUi(state, actionReceiver)
    }
}

@Composable
private fun ViewingUi(state: MeasurementState.Viewing, actionReceiver: ActionReceiver) {
    Column(Modifier.fillMaxSize()) {
        when (state.data) {
            null -> {
                Text("No data")
            }
            else -> {
                Text("For: ${state.data.date.format(DateTimeFormatter.ISO_DATE)}")
                for ((name, amount) in state.data.measurements) {
                    Text("$name: $amount")
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
        Button({ actionReceiver.receive(MeasurementAction.Show) }) {
            Text("OK")
        }
    }
}