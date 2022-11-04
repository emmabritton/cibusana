package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.flow.common.LogOutUser
import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.reduceLoginAction
import app.emmabritton.cibusana.flow.register.RegisterAction
import app.emmabritton.cibusana.flow.register.reduceRegisterAction
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.flow.welcome.reduceWelcomeAction
import app.emmabritton.system.Action
import app.emmabritton.system.CommandException
import timber.log.Timber

fun reduce(action: Action, state: AppState): AppEffect {
    return when (action) {
        is LoginAction -> reduceLoginAction(action, state)
        is WelcomeAction -> reduceWelcomeAction(action, state)
        is RegisterAction -> reduceRegisterAction(action, state)
        is GlobalAction -> reduceGlobalAction(action, state)
        is CommandException -> {
            Timber.e(action.cause, action.name)
            AppEffect(
                AppState.init()
                    .copy(error = "Command crashed\n${action.name}\n${action.cause.javaClass.simpleName}\n${action.cause.stackTraceToString()}"),
                emptyList()
            )
        }
        is UnknownUiState -> action.toEffect()
        else -> UnknownAction(action.describe())
    }
}

fun reduceGlobalAction(action: GlobalAction, state: AppState): AppEffect {
    return when (action) {
        is GlobalAction.LoggedIn -> AppEffect(
            state.copy(uiState = HomeState(), user = action.user),
            emptyList()
        )
        GlobalAction.ServerRejectedToken -> AppEffect(
            state.copy(
                uiState = WelcomeState,
                user = null
            ), listOf(LogOutUser())
        )
    }
}