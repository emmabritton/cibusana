package app.emmabritton.cibusana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Render
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.login.*
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import kotlinx.coroutines.flow.MutableStateFlow

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiState = MutableStateFlow(AppState.init())
        val runtime = Runtime { uiState.value = it }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!runtime.onBackPressed()) {
                    finish()
                }
            }
        })

        setContent {
            CibusanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        val state = uiState.collectAsState().value
                        Render(state, runtime, Modifier.padding(it))
                    }

                }
            }
        }
    }
}

