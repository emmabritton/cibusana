package app.emmabritton.cibusana.system.login

import app.emmabritton.cibusana.system.UiState

sealed class LoginState: UiState {
    data class Entering(val email: String, val password: String): LoginState() {
        fun toLoading() = Loading(this)
        companion object {
            fun init(): Entering {
                return Entering("", "")
            }
        }
    }

    data class Loading(val details: Entering): LoginState() {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering): LoginState() {
        fun toEntering() = details
    }

    data class LoggedIn(val name: String, val token: String): LoginState()
}