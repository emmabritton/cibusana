package app.emmabritton.cibusana.system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.cibusana.system.register.RegisterState
import app.emmabritton.cibusana.system.welcome.WelcomeState
import app.emmabritton.cibusana.ui.LoginUi
import app.emmabritton.cibusana.ui.RegisterUi
import app.emmabritton.cibusana.ui.WelcomeUi
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.system.ActionReceiver

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
                else -> actionReceiver.receive(UnknownUiState(state.uiState.javaClass.simpleName))
            }
        }
    }
}