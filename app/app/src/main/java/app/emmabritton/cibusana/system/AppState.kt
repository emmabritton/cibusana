package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.State

data class AppState(
    val error: String?,
    val uiState: UiState,
    val uiHistory: List<UiState>,
    val user: User?
) : State {
    override fun toString(): String {
        return if (error != null) {
            "Error: $error"
        } else {
            uiState.javaClass.simpleName
        }
    }

    companion object {
        fun init(): AppState {
            val uiState = SplashState
            return AppState(null, uiState, emptyList(), null)
        }
    }
}


interface UiState {
    val config: UiStateConfig
}

data class UiStateConfig(
    /**
     * If false, pressing back is ignored
     */
    val isBackEnabled: Boolean,
    /**
     * If true, this clears the AppState.uiHistory when added
     */
    val clearUiHistory: Boolean,
    /**
     * If true, and the last entry in AppState.uiHistory is the same class
     * then this replaces it
     */
    val replaceDuplicate: Boolean,
    /**
     * If true, then this uiState will be added to AppState.uiHistory
     */
    val addToHistory: Boolean
) {
    companion object {
        fun originScreen() = UiStateConfig(true, true, true, true)
        fun loadingScreen() = UiStateConfig(false, false, true, false)
        fun generalScreen() = UiStateConfig(true, false, true, true)
        fun tempScreen() = UiStateConfig(true, false, true, false)
    }
}

