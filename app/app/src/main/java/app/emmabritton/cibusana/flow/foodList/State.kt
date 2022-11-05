package app.emmabritton.cibusana.flow.foodList

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

data class FoodState(val food: List<FoodResponse>, val loading: Boolean, val searchTerm: String, val scrollY: Int) : UiState {
    override val config = UiStateConfig.generalScreen()

    companion object {
        fun init() = FoodState(emptyList(), true, "", 0)
    }
}