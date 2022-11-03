package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.system.login.LoginAction
import app.emmabritton.cibusana.system.login.reduceLoginAction
import app.emmabritton.system.Action
import app.emmabritton.system.CommandException
import timber.log.Timber

fun reduce(action: Action, state: AppState): AppEffect {
    return when (action) {
        is LoginAction -> reduceLoginAction(action, state)
        is CommandException -> {
            Timber.e(action.cause,action.name)
            AppEffect(
                AppState.init()
                    .copy(error = "Command crashed\n${action.name}\n${action.cause.javaClass.simpleName}\n${action.cause.stackTraceToString()}"),
                emptyList()
            )
        }
        else -> InvalidAction(action.describe())
    }
}
