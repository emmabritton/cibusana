package app.emmabritton.cibusana.system.register

import app.emmabritton.cibusana.system.UiState

sealed class RegisterState(previousState: UiState?) : UiState(previousState, false) {
    data class Entering(
        val email: String,
        val password: String,
        val name: String,
        private val lastScreen: UiState?
    ) : RegisterState(lastScreen) {
        fun toLoading() = Loading(this)

        companion object {
            fun init(previousState: UiState?): Entering {
                return Entering("", "", "", previousState)
            }
        }
    }

    data class Loading(val details: Entering) : RegisterState(null) {
        fun toError(msg: String) = Error(msg, details)
    }

    data class Error(val message: String, val details: Entering) : RegisterState(details) {
        fun toEntering() = details
    }

    data class Registered(val name: String, val token: String, private val lastScreen: UiState?) :
        RegisterState(lastScreen)
}