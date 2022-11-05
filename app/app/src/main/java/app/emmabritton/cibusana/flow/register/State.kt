package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

sealed class RegisterState(override val config: UiStateConfig) : UiState {
    /**
     * Entering data into register screen
     */
    data class Entering(
        val email: String,
        val password: String,
        val name: String
    ) : RegisterState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init(): Entering {
                return Entering("", "", "")
            }
        }
    }

    /**
     * Submitting register request to server
     */
    data class Loading(val details: Entering) : RegisterState(UiStateConfig.loadingScreen()) {
        fun toError(msg: String) = Error(msg, details)
    }

    /**
     * Something went wrong with register request
     */
    data class Error(val message: String, val details: Entering) : RegisterState(UiStateConfig.tempScreen()) {
        fun toEntering() = details
    }
}