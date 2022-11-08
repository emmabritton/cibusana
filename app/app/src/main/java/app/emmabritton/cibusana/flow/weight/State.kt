package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import java.time.ZonedDateTime

data class Range(val start: ZonedDateTime, val end: ZonedDateTime) {
    companion object {
        fun init() = Range(ZonedDateTime.now().minusDays(30), ZonedDateTime.now())
    }
}

sealed class WeightState(override val config: UiStateConfig) : UiState {
    data class Viewing(
        val weights: Map<ZonedDateTime, Float>,
        val range: Range,
        val newAmount: Float,
        val newDate: ZonedDateTime
    ) :
        WeightState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init() = Viewing(emptyMap(), Range.init(), 100f, ZonedDateTime.now())
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
