package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import app.emmabritton.cibusana.system.loggedInBarConfig

object HomeState : UiState {
    override val config = UiStateConfig.originScreen()
    override val topBarConfig: TopBarConfig = loggedInBarConfig(R.string.screen_home)
}