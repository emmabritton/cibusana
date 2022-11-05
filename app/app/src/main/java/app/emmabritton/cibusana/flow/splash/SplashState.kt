package app.emmabritton.cibusana.flow.splash

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

/**
 * Splash screen is visible
 */
object SplashState : UiState {
    override val config = UiStateConfig.loadingScreen()
}