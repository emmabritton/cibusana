package app.emmabritton.cibusana.flow.login

import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.system.FabConfig
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

sealed class LoginState(
    override val config: UiStateConfig,
    override val topBarConfig: TopBarConfig = TopBarConfig.loggedOut(R.string.screen_login),
    override val fabAction: FabConfig? = null
) : UiState {
    /**
     * Entering data into login screen
     */
    data class Entering(val email: String, val password: String) :
        LoginState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init(): Entering {
                return Entering("", "")
            }
        }
    }

    /**
     * Submitting login request to server
     */
    data class Loading(val details: Entering) : LoginState(UiStateConfig.loadingScreen()) {
        fun toError(msg: String) = Error(msg, details)
    }

    /**
     * Something went wrong with login request
     */
    data class Error(val message: String, val details: Entering) :
        LoginState(UiStateConfig.tempScreen()) {
        fun toEntering() = details
    }
}