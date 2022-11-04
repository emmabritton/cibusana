package app.emmabritton.cibusana.system.login

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

sealed class LoginState(config: UiStateConfig) : UiState(config) {
    data class Entering(val email: String, val password: String,private val lastScreen: UiState?): LoginState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)
        companion object {
            fun init(previousState: UiState?): Entering {
                return Entering("", "", previousState)
            }
        }
    }

    data class Loading(val details: Entering): LoginState(UiStateConfig.loadingScreen()) {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering): LoginState(UiStateConfig.tempScreen()) {
        fun toEntering() = details
    }

    data class LoggedIn(val name: String, val token: String): LoginState(UiStateConfig.originScreen())
}