package app.emmabritton.cibusana.system.welcome

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.cibusana.system.register.RegisterState

fun reduceWelcomeAction(action: WelcomeAction, state: AppState): AppEffect {
    return when (action) {
        WelcomeAction.UserPressedLogin -> AppEffect(state.copy(uiState = LoginState.Entering.init()), emptyList())
        WelcomeAction.UserPressedRegister -> AppEffect(state.copy(uiState = RegisterState.Entering.init()), emptyList())
    }
}