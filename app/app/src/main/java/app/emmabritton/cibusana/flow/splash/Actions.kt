package app.emmabritton.cibusana.flow.splash

import app.emmabritton.system.Action

sealed class SplashAction : Action {
    /**
     * The app has started
     */
    object InitialiseApp : SplashAction()
}