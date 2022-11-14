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
import app.emmabritton.cibusana.system.TitleType
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.ui.SmallLogo
import app.emmabritton.cibusana.ui.theme.CibusanaTheme
import app.emmabritton.system.ActionReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.java.KoinJavaComponent.inject

class AppActivity : ComponentActivity() {
    private val dateTimePrinter: DateTimePrinter by inject(DateTimePrinter::class.java)

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
                            state.uiState.topBarConfig?.let { config ->
                                TopBar(runtime, config, dateTimePrinter)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(actionReceiver: ActionReceiver, config: TopBarConfig, printer: DateTimePrinter) {
    val titleText = config.title
    TopAppBar(
        title = {
            when (titleText) {
                is TitleType.Fmt -> Text(
                    stringResource(
                        titleText.strResId,
                        *titleText.args.toTypedArray()
                    )
                )
                is TitleType.Res -> Text(stringResource(titleText.strResId))
                is TitleType.Str -> Text(titleText.text)
                is TitleType.Date -> Text(printer.formatDate(titleText.dt))
            }
        },
        navigationIcon = {
            SmallLogo(
                actionReceiver,
                config.navTargetAction
            )
        })
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
