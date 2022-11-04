package app.emmabritton.cibusana.flow.splash

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

object SplashState : UiState {
    override val config = UiStateConfig.loadingScreen()
}