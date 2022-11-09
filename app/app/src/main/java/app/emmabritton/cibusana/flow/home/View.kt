package app.emmabritton.cibusana.flow.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.flow.entry.EntryAction
import app.emmabritton.cibusana.flow.foodList.FoodAction
import app.emmabritton.cibusana.flow.weight.WeightAction
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.system.ActionReceiver

//state.uiState === uiState, but this approach avoids having to cast it
@Composable
fun HomeUi(
    state: AppState,
    uiState: HomeState,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize().then(modifier)) {
        Text("Hi, ${state.user?.name}")

        Button({actionReceiver.receive(FoodAction.Show)}) {
            Text("View food")
        }

        Button({actionReceiver.receive(WeightAction.Show)}) {
            Text("View weight")
        }

        Button({actionReceiver.receive(EntryAction.Show)}) {
            Text("View entries")
        }
    }
}