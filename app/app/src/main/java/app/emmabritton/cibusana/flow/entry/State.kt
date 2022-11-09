package app.emmabritton.cibusana.flow.entry

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.network.models.MealResponse
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import java.time.ZonedDateTime
import java.util.UUID

sealed class EntryState(override val config: UiStateConfig) : UiState {
    data class Viewing(
        val food: Map<UUID, FoodResponse>,
        val meal: Map<UUID, MealResponse>,
        val entries: Map<String, List<MealEntryResponse>>,
        val date: ZonedDateTime
    ) : EntryState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)
    }

    data class Loading(val viewing: Viewing) : EntryState(UiStateConfig.loadingScreen()) {
        fun toError() = Error(viewing)

        companion object {
            fun init() = Loading(Viewing(emptyMap(), emptyMap(), emptyMap(), ZonedDateTime.now()))
        }
    }

    data class Error(val viewing: Viewing) : EntryState(UiStateConfig.tempScreen())
}