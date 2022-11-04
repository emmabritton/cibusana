package app.emmabritton.cibusana.flow.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.cibusana.ui.theme.Dimen.Padding
import app.emmabritton.system.ActionReceiver

@Composable
fun LoginUi(state: LoginState, actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    when (state) {
        is LoginState.Entering -> EnteringUi(
            state = state,
            actionReceiver = actionReceiver,
            modifier
        )
        is LoginState.Error -> ErrorUi(state = state, actionReceiver = actionReceiver, modifier)
        is LoginState.Loading -> LoadingUi(modifier)
    }
}

@Composable
private fun ErrorUi(
    state: LoginState.Error,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Errors: ${state.message}")
        TextButton(onClick = { actionReceiver.receive(LoginAction.UserClearedError) }) {
            Text("OK")
        }
    }
}

@Composable
private fun LoadingUi(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnteringUi(
    state: LoginState.Entering,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.Full)
            .then(modifier),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            stringResource(id = R.string.login_title),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(Dimen.Space.Title))
        TextField(
            value = state.email,
            onValueChange = {
                actionReceiver.receive(
                    LoginAction.UserUpdatedEmail(it)
                )
            },
            label = { Text(stringResource(id = R.string.login_email)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimen.Space.Form))
        TextField(
            value = state.password,
            onValueChange = {
                actionReceiver.receive(
                    LoginAction.UserUpdatedPassword(it)
                )
            },
            label = { Text(stringResource(id = R.string.login_password)) },
            keyboardActions = KeyboardActions(onDone = { actionReceiver.receive(LoginAction.UserSubmitted) }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimen.Space.FormAction))
        Button(onClick = { actionReceiver.receive(LoginAction.UserSubmitted) }) {
            Text(stringResource(id = R.string.login_submit))
        }
    }
}
