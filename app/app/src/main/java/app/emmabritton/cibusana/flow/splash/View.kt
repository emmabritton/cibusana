package app.emmabritton.cibusana.flow.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.emmabritton.cibusana.ui.LargeLogo
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import app.emmabritton.cibusana.ui.theme.Dimen

@Composable
fun SplashUi(modifier: Modifier = Modifier) {
    Column(
        Modifier
            .fillMaxSize()
            .then(modifier), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        LargeLogo()
        Spacer(Modifier.height(Dimen.Space.Title))
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun PreviewSplashUi() {
    CibusanaTheme {
        Surface {
            SplashUi()
        }
    }
}