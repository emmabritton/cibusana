package app.emmabritton.cibusana.flow

import app.emmabritton.cibusana.flow.common.CommonAction
import app.emmabritton.cibusana.flow.common.reduceCommonAction
import app.emmabritton.cibusana.flow.foodList.FoodAction
import app.emmabritton.cibusana.flow.foodList.reduceFoodAction
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.reduceLoginAction
import app.emmabritton.cibusana.flow.register.RegisterAction
import app.emmabritton.cibusana.flow.register.reduceRegisterAction
import app.emmabritton.cibusana.flow.splash.SplashAction
import app.emmabritton.cibusana.flow.splash.reduceSplashAction
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.reduceWelcomeAction
import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.UnknownAction
import app.emmabritton.cibusana.system.UnknownUiState
import app.emmabritton.system.Action
import app.emmabritton.system.CommandException
import timber.log.Timber

fun reduce(action: Action, state: AppState): AppEffect {
    return when (action) {
        is LoginAction -> reduceLoginAction(action, state)
        is WelcomeAction -> reduceWelcomeAction(action, state)
        is RegisterAction -> reduceRegisterAction(action, state)
        is CommonAction -> reduceCommonAction(action, state)
        is SplashAction -> reduceSplashAction(action, state)
        is FoodAction -> reduceFoodAction(action, state)
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

