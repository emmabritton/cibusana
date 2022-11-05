package app.emmabritton.cibusana.flow.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Hi, ${state.user?.name}")
    }
}