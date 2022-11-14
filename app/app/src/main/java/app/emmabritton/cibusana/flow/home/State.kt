package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.system.TitleType
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import java.time.ZonedDateTime
import java.util.*

sealed class HomeState(
    val date: ZonedDateTime,
    override val config: UiStateConfig,
    override val topBarConfig: TopBarConfig = object : TopBarConfig {
        override val title = TitleType.Date(date)
        override val navTargetAction = HomeAction.ShowToday
    }
) : UiState {
    class Loading(date: ZonedDateTime) : HomeState(date, UiStateConfig.loadingScreen()) {
        fun toError() = Error(date)
    }

    class Viewing(date: ZonedDateTime, val data: Map<String, List<MealEntryResponse>>, val food: Map<UUID, FoodResponse>) :
        HomeState(date, UiStateConfig.generalScreen())

    class Error(date: ZonedDateTime) : HomeState(date, UiStateConfig.generalScreen())
}
