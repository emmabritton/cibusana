package app.emmabritton.cibusana.flow.welcome

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.register.RegisterState

fun reduceWelcomeAction(action: WelcomeAction, state: AppState): AppEffect {
    return when (action) {
        WelcomeAction.UserPressedLogin -> AppEffect(state.copy(uiState = LoginState.Entering.init(state.uiState)), emptyList())
        WelcomeAction.UserPressedRegister -> AppEffect(state.copy(uiState = RegisterState.Entering.init(state.uiState)), emptyList())
        WelcomeAction.Show -> AppEffect(state.copy(uiState = WelcomeState), emptyList())
    }
}