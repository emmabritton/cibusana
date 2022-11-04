package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

sealed class RegisterState(override val config: UiStateConfig) : UiState {
    data class Entering(
        val email: String,
        val password: String,
        val name: String,
        private val lastScreen: UiState?
    ) : RegisterState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init(previousState: UiState?): Entering {
                return Entering("", "", "", previousState)
            }
        }
    }

    data class Loading(val details: Entering) : RegisterState(UiStateConfig.loadingScreen()) {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering) : RegisterState(UiStateConfig.tempScreen()) {
        fun toEntering() = details
    }
}