package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.system.welcome.WelcomeState
import app.emmabritton.system.State

data class AppState(
    val error: String?,
    val uiState: UiState,
) : State {
    companion object {
        fun init(): AppState {
            return AppState(null, WelcomeState)
        }
    }
}

interface UiState

