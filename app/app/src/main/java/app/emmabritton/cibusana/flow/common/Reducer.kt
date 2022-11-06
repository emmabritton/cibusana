package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import timber.log.Timber

fun reduceCommonAction(action: CommonAction, state: AppState): AppEffect {
    return when (action) {
        is CommonAction.LoggedIn -> AppEffect(
            state.copy(uiState = HomeState, user = action.user),
            emptyList()
        )
        CommonAction.ServerRejectedToken -> AppEffect(
            state.copy(
                uiState = WelcomeState,
                user = null
            ), listOf(LogOutUser())
        )
        CommonAction.GoBack -> {
            var newState = state.copy()
            //handle if invalid because back pressed twice or something
            val current = newState.uiHistory.removeLast()
            newState = newState.copy(uiState = newState.uiHistory.last())
            Timber.i("Going back (dropping ${current.javaClass.simpleName})")
            AppEffect(newState, emptyList())
        }
    }
}