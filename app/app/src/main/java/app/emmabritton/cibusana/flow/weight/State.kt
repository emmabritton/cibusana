package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.DateRange
import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.network.models.WeightResponse
import app.emmabritton.cibusana.system.FabConfig
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import java.time.ZonedDateTime

sealed class WeightState(
    override val config: UiStateConfig,
    override val topBarConfig: TopBarConfig = TopBarConfig.loggedIn(R.string.screen_weight),
    override val fabAction: FabConfig? = null
) : UiState {
    data class Viewing(
        val weights: List<WeightResponse>,
        val range: DateRange,
        val newAmount: Int,
        val newDate: ZonedDateTime
    ) : WeightState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init() = Viewing(emptyList(), DateRange.init(), 100000, ZonedDateTime.now())
        }
    }

    data class Loading(val viewing: Viewing) :
        WeightState(UiStateConfig.loadingScreen()) {
        fun toViewing() = viewing

        companion object {
            fun init() = Loading(
                Viewing.init()
            )
        }
    }

    object Error : WeightState(UiStateConfig.tempScreen())
}
