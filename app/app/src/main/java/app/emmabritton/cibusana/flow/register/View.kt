package app.emmabritton.cibusana.flow.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.cibusana.ui.theme.Dimen.Padding
import app.emmabritton.system.ActionReceiver

@Composable
fun RegisterUi(
    state: RegisterState,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    when (state) {
        is RegisterState.Entering -> EnteringUi(
            state = state,
            actionReceiver = actionReceiver,
            modifier
        )
        is RegisterState.Error -> ErrorUi(state = state, actionReceiver = actionReceiver, modifier)
        is RegisterState.Loading -> LoadingUi(modifier)
    }
}

@Composable
private fun ErrorUi(
    state: RegisterState.Error,
    actionReceiver: ActionReceiver,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Errors: ${state.message}")
            TextButton(onClick = { actionReceiver.receive(RegisterAction.UserClearedError) }) {
                Text("OK")
            }
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
    state: RegisterState.Entering,
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
        Spacer(Modifier.height(Dimen.Space.Title))
        TextField(
            value = state.name,
            onValueChange = {
                actionReceiver.receive(
                    RegisterAction.UserUpdatedName(it)
                )
            },
            label = { Text(stringResource(id = R.string.register_name)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimen.Space.Form))
        TextField(
            value = state.email,
            onValueChange = {
                actionReceiver.receive(
                    RegisterAction.UserUpdatedEmail(it)
                )
            },
            label = { Text(stringResource(id = R.string.register_email)) },
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
                    RegisterAction.UserUpdatedPassword(it)
                )
            },
            label = { Text(stringResource(id = R.string.register_password)) },
            placeholder = { Text(stringResource(id = R.string.register_password_placeholder)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimen.Space.Form))
        TextField(
            value = state.height.toString(),
            onValueChange = {
                actionReceiver.receive(
                    RegisterAction.UserUpdatedHeight(it.toInt())
                )
            },
            label = { Text(stringResource(id = R.string.register_height)) },
            keyboardActions = KeyboardActions(onDone = { actionReceiver.receive(RegisterAction.UserSubmitted) }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimen.Space.FormAction))
        Button(onClick = { actionReceiver.receive(RegisterAction.UserSubmitted) }) {
            Text(stringResource(id = R.string.register_submit))
        }
    }
}