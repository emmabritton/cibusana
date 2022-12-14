package app.emmabritton.cibusana.flow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.flow.foodList.FoodState
import app.emmabritton.cibusana.flow.foodList.FoodUi
import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.home.HomeUi
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.login.LoginUi
import app.emmabritton.cibusana.flow.measurements.MeasurementState
import app.emmabritton.cibusana.flow.measurements.MeasurementUi
import app.emmabritton.cibusana.flow.register.RegisterState
import app.emmabritton.cibusana.flow.register.RegisterUi
import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.flow.splash.SplashUi
import app.emmabritton.cibusana.flow.weight.WeightState
import app.emmabritton.cibusana.flow.weight.WeightUi
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.flow.welcome.WelcomeUi
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.UnknownUiState
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.system.ActionReceiver

@ExperimentalMaterial3Api
@Composable
fun Render(state: AppState, actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        if (state.error != null) {
            Text(
                text = state.error,
                modifier = Modifier.padding(Dimen.Padding.Normal),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            when (state.uiState) {
                is WelcomeState -> WelcomeUi(actionReceiver = actionReceiver)
                is LoginState -> LoginUi(
                    state = state.uiState,
                    actionReceiver = actionReceiver
                )
                is RegisterState -> RegisterUi(
                    state = state.uiState,
                    actionReceiver = actionReceiver
                )
                is HomeState -> HomeUi(
                    uiState = state.uiState,
                    actionReceiver = actionReceiver
                )
                is WeightState -> WeightUi(
                    weightState = state.uiState,
                    actionReceiver = actionReceiver
                )
                is MeasurementState -> MeasurementUi(
                    state = state.uiState,
                    actionReceiver = actionReceiver
                )
                is SplashState -> SplashUi()
                is FoodState -> FoodUi(foodState = state.uiState, actionReceiver = actionReceiver)
                else -> actionReceiver.receive(UnknownUiState(state.uiState.javaClass.simpleName))
            }
        }
    }
}