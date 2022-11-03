package app.emmabritton.cibusana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.login.*
import app.emmabritton.cibusana.ui.LoginUi
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import kotlinx.coroutines.flow.MutableStateFlow

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiState = MutableStateFlow(AppState.init())
        val runtime = Runtime { uiState.value = it }

        setContent {
            CibusanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        val state = uiState.collectAsState().value
                        Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {
                            if (state.error != null) {
                                Text(text = state.error, modifier = Modifier.padding(8.dp), fontSize = MaterialTheme.typography.bodySmall.fontSize)
                            } else {
                                LoginUi(state.loginState, runtime)
                            }
                        }
                    }

                }
            }
        }
    }
}

