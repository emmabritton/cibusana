package app.emmabritton.cibusana.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.flow.ToolingAction
import app.emmabritton.cibusana.flow.ToolingActionReceiver
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import app.emmabritton.system.Action
import app.emmabritton.system.ActionReceiver

@Composable
fun SmallLogo(actionReceiver: ActionReceiver, navAction: Action, modifier: Modifier = Modifier) {
    IconButton(onClick = { actionReceiver.receive(navAction) }) {
        Image(
            painterResource(R.drawable.logo),
            contentDescription = stringResource(id = R.string.app_name),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop),
            modifier = Modifier
                .width(50.dp)
                .then(modifier)
        )
    }
}

@Composable
fun LargeLogo(modifier: Modifier = Modifier) {
    Image(
        painterResource(R.drawable.logo),
        contentDescription = stringResource(id = R.string.app_name),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop),
        modifier = Modifier
            .width(250.dp)
            .then(modifier)
    )
}

@Composable
fun TextLogo(modifier: Modifier = Modifier) {
    Text(
        stringResource(id = R.string.app_name),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .then(modifier)
    )
}

@Preview
@Composable
private fun PreviewDarkLogos() {
    CibusanaTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Preview
@Composable
private fun PreviewLightLogos() {
    CibusanaTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Composable
private fun PreviewContent() {
    Surface(
        Modifier
            .padding(8.dp)
            .width(300.dp)
            .height(200.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            SmallLogo(ToolingActionReceiver, ToolingAction)
            LargeLogo()
            TextLogo()
        }
    }
}