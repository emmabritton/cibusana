package app.emmabritton.cibusana.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.emmabritton.cibusana.system.login.*
import app.emmabritton.system.ActionReceiver

@Composable
fun LoginUi(state: LoginState, actionReceiver: ActionReceiver) {
    when (state) {
        is LoginState.Entering -> EnteringUi(state = state, actionReceiver = actionReceiver)
        is LoginState.Error -> ErrorUi(state = state, actionReceiver = actionReceiver)
        is LoginState.Loading -> LoadingUi()
        is LoginState.LoggedIn -> LoggedInUi(name = state.name)
    }
}

@Composable
fun ErrorUi(state: LoginState.Error, actionReceiver: ActionReceiver) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Errors: ${state.message}")
        TextButton(onClick = {actionReceiver.receive(UserClearedError)}) {
            Text("OK")
        }
    }
}

@Composable
fun LoadingUi() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnteringUi(state: LoginState.Entering, actionReceiver: ActionReceiver) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        TextField(value = state.email, onValueChange = {
            actionReceiver.receive(
                UserUpdatedLoginEmail(it)
            )
        }, placeholder = { Text("Email") })
        TextField(value = state.password, onValueChange = {
            actionReceiver.receive(
                UserUpdatedLoginPassword(it)
            )
        }, placeholder = { Text("Password") })
        TextButton(onClick = { actionReceiver.receive(UserSubmittedLogin) }) {
            Text("Login")
        }
    }
}

@Composable
fun LoggedInUi(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Hello ${name}")
    }
}
