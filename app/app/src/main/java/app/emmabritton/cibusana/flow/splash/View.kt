package app.emmabritton.cibusana.flow.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.ui.theme.Dimen

@Composable
fun SplashUi(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(id = R.string.app_name))
        Spacer(Modifier.height(Dimen.Space.Title))
        CircularProgressIndicator()
    }
}