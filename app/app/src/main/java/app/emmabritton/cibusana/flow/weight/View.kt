package app.emmabritton.cibusana.flow.weight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.emmabritton.cibusana.ui.theme.Dimen.Padding
import app.emmabritton.cibusana.ui.theme.Dimen.Space
import app.emmabritton.system.ActionReceiver
import java.time.format.DateTimeFormatter

@Composable
fun WeightUi(weightState: WeightState, actionReceiver: ActionReceiver) {
    when (weightState) {
        is WeightState.Viewing -> ViewingUi(
            weightState = weightState,
            actionReceiver = actionReceiver
        )
        is WeightState.Loading -> LoadingUi()
        WeightState.Error -> ErrorUi(actionReceiver = actionReceiver)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewingUi(weightState: WeightState.Viewing, actionReceiver: ActionReceiver) {
    Column(Modifier.fillMaxSize()) {
        Text("Start: ${weightState.range.start.format(DateTimeFormatter.ISO_DATE)}")
        Text("End: ${weightState.range.end.format(DateTimeFormatter.ISO_DATE)}")
        Row {
            TextField(
                value = weightState.newAmount.toString(),
                onValueChange = { actionReceiver.receive(WeightAction.UserChangedAmount(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text("Weight (KGs)") },
                keyboardActions = KeyboardActions(onDone = { actionReceiver.receive(WeightAction.SubmitAmount) }),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(Space.FormAction))
            Button(onClick = { actionReceiver.receive(WeightAction.SubmitAmount) }) {
                Text("Submit")
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(weightState.weights.toList()) {
                Text("${it.first.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)} ${it.second}kg", modifier = Modifier.padding(Padding.Small))
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