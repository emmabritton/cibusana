package app.emmabritton.cibusana.flow.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.flow.ToolingActionReceiver
import app.emmabritton.cibusana.ui.LargeLogo
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import app.emmabritton.cibusana.ui.theme.Dimen
import app.emmabritton.system.ActionReceiver

private val ButtonWidth = 150.dp
private val ButtonHeight = 60.dp

@Composable
fun WelcomeUi(actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeLogo()
        Spacer(modifier = Modifier.height(Dimen.Space.Title))
        TextButton(
            onClick = { actionReceiver.receive(WelcomeAction.UserPressedLogin) },
            modifier = Modifier.defaultMinSize(ButtonWidth, ButtonHeight)
        ) {
            Text(stringResource(id = R.string.welcome_login))
        }
        Spacer(modifier = Modifier.height(Dimen.Space.Form))
        TextButton(
            onClick = { actionReceiver.receive(WelcomeAction.UserPressedRegister) },
            modifier = Modifier.defaultMinSize(ButtonWidth, ButtonHeight)
        ) {
            Text(stringResource(id = R.string.welcome_register))
        }
    }
}

@Preview
@Composable
private fun PreviewWelcomeUi() {
    CibusanaTheme {
        Surface {
            WelcomeUi(ToolingActionReceiver)
        }
    }
}