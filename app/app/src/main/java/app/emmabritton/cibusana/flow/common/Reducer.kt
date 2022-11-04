package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState

fun reduceCommonAction(action: CommonAction, state: AppState): AppEffect {
    return when (action) {
        is CommonAction.LoggedIn -> AppEffect(
            state.copy(uiState = HomeState(), user = action.user),
            emptyList()
        )
        CommonAction.ServerRejectedToken -> AppEffect(
            state.copy(
                uiState = WelcomeState,
                user = null
            ), listOf(LogOutUser())
        )
        CommonAction.InitialiseApp -> AppEffect(state, listOf(LoadUserData()))
    }
}