package app.emmabritton.cibusana.flow.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.emmabritton.system.ActionReceiver

@Composable
fun HomeUi(
    uiState: HomeState,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is HomeState.Loading -> LoadingUi(modifier = modifier)
        is HomeState.Viewing -> ViewingUi(
            state = uiState,
            actionReceiver = actionReceiver,
            modifier = modifier
        )
        is HomeState.Error -> ErrorUi(actionReceiver = actionReceiver, modifier = modifier)
    }
}

@Composable
private fun ViewingUi(
    state: HomeState.Viewing,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Row(Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { actionReceiver.receive(HomeAction.UserPressedPrevDay) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Prev")
            }
            TextButton(
                onClick = { actionReceiver.receive(HomeAction.UserPressedNextDay) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
        Text("Total calories: ${state.data.values.sumOf { list -> list.sumOf { it.calories } }}kcal")
        for (mealTime in state.data.keys) {
            Text(mealTime)
            Column {
                for (item in state.data[mealTime] ?: emptyList()) {
                    val food = state.food[item.foodId]
                    Text("${food?.name} ${item.grams}g ${item.calories}kcal")
                }
            }
        }
    }
}

@Composable
private fun LoadingUi(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorUi(actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error")
        Button({ actionReceiver.receive(HomeAction.ShowToday) }) {
            Text("OK")
        }
    }
}