package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.system.State

data class AppState(
    val error: String?,
    val loginState: LoginState,
) : State {
    companion object {
        fun init(): AppState {
            return AppState(null, LoginState.Entering("", ""))
        }
    }
}

