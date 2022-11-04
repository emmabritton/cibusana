package app.emmabritton.cibusana.system.register

import app.emmabritton.cibusana.system.UiState

sealed class RegisterState: UiState {
    data class Entering(val email: String, val password: String, val name: String): RegisterState() {
        fun toLoading() = Loading(this)
        companion object {
            fun init(): Entering {
                return Entering("", "", "")
            }
        }
    }

    data class Loading(val details: Entering): RegisterState() {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering): RegisterState() {
        fun toEntering() = details
    }

    data class Registered(val name: String, val token: String): RegisterState()
}