package app.emmabritton.cibusana.flow.welcome

import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

/**
 * Welcome screen is visible
 */
object WelcomeState : UiState {
    override val config = UiStateConfig.originScreen()
    override val topBarConfig: TopBarConfig? = null
}