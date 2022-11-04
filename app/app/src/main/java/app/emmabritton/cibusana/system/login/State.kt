package app.emmabritton.cibusana.system.login

import app.emmabritton.cibusana.system.UiState

sealed class LoginState(previousState: UiState?) : UiState(previousState, false) {
    data class Entering(val email: String, val password: String,private val lastScreen: UiState?): LoginState(lastScreen) {
        fun toLoading() = Loading(this)
        companion object {
            fun init(previousState: UiState?): Entering {
                return Entering("", "", previousState)
            }
        }
    }

    data class Loading(val details: Entering): LoginState(null) {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering): LoginState(details) {
        fun toEntering() = details
    }

    data class LoggedIn(val name: String, val token: String, private val lastScreen: UiState?): LoginState(lastScreen)
}