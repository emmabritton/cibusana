package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.system.welcome.WelcomeState
import app.emmabritton.system.State

data class AppState(
    val error: String?,
    val uiState: UiState,
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
            return AppState(null, WelcomeState)
        }
    }
}


open class UiState(
    /**
     * When back is pressed the ui state should be reset to this
     */
    val previousState: UiState?,
    /**
     * When back is pressed and previousState is null
     * and isFirstScreen is true, then close app
     */
    val isFirstScreen: Boolean
)

