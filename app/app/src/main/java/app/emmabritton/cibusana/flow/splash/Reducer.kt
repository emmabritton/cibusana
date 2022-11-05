package app.emmabritton.cibusana.flow.splash

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState

fun reduceSplashAction(action: SplashAction, state: AppState): AppEffect {
    return when (action) {
        SplashAction.InitialiseApp -> AppEffect(state, listOf(PreLoadCacheData(), LoadUserData()))
    }
}