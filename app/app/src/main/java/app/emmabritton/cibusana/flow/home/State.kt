package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.system.*
import java.time.ZonedDateTime
import java.util.*

sealed class HomeState(
    val date: ZonedDateTime,
    override val config: UiStateConfig,
    override val topBarConfig: TopBarConfig = TopBarConfig(
        TitleType.Date(date),
        HomeAction.ShowToday
    ),
    override val fabAction: FabConfig? = null
) : UiState {
    class Loading(date: ZonedDateTime) : HomeState(date, UiStateConfig.loadingScreen()) {
        fun toError() = Error(date)
    }

    class Viewing(
        date: ZonedDateTime,
        val data: Map<String, List<MealEntryResponse>>,
        val food: Map<UUID, FoodResponse>
    ) :
        HomeState(date, UiStateConfig.generalScreen())

    class Error(date: ZonedDateTime) : HomeState(date, UiStateConfig.generalScreen())
}
