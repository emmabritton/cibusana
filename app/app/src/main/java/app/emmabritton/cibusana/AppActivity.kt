package app.emmabritton.cibusana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.emmabritton.cibusana.flow.Render
import app.emmabritton.cibusana.flow.splash.SplashAction
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.ui.SmallLogo
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import kotlinx.coroutines.flow.MutableStateFlow

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiState = MutableStateFlow(AppState.init())
        val runtime = Runtime { uiState.value = it }
        runtime.receive(SplashAction.InitialiseApp)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!runtime.onBackPressed()) {
                    finish()
                }
            }
        })

        setContent {
            CibusanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state = uiState.collectAsState().value
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            state.uiState.topBarConfig?.let { barState ->
                                TopAppBar(
                                    title = {
                                        Text(stringResource(barState.title))
                                    },
                                    navigationIcon = {
                                        SmallLogo(
                                            runtime,
                                            barState.navTargetAction
                                        )
                                    })
                            }
                        },
                        content = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(it)
                            ) {
                                Render(state, runtime)
                                if (BuildConfig.DEBUG) {
                                    DebugView(state)
                                }
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun DebugView(state: AppState) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column {
            if (state.uiHistory.isNotEmpty()) {
                for (history in state.uiHistory.asReversed()) {
                    Text(history.javaClass.simpleName, style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text("No history", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
