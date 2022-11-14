package app.emmabritton.cibusana.flow.splash

import app.emmabritton.cibusana.system.FabConfig
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

/**
 * Splash screen is visible
 */
object SplashState : UiState {
    override val config = UiStateConfig.loadingScreen()
    override val topBarConfig: TopBarConfig? = null
    override val fabAction: FabConfig? = null
}