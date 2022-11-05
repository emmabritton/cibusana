package app.emmabritton.cibusana.flow.foodList

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState

fun reduceFoodAction(foodAction: FoodAction, state: AppState): AppEffect {
    return when (foodAction) {
        is FoodAction.UserUpdateSearchTerm -> {
            //cancel search command
            AppEffect(
                state.copy(
                    uiState = (state.uiState as FoodState).copy(
                        loading = true,
                        searchTerm = foodAction.newText
                    )
                ), listOf(SearchForFood(foodAction.newText))
            )
        }
        is FoodAction.ReplaceFood -> {
            val foodState = (state.uiState as FoodState)
            AppEffect(
                state.copy(
                    uiState = foodState.copy(
                        loading = false,
                        food = foodAction.items
                    )
                ), emptyList()
            )
        }
        is FoodAction.SearchRejected -> {
            //TODO handle error
            AppEffect(state, emptyList())
        }
        FoodAction.Show -> AppEffect(state.copy(uiState = FoodState.init()), listOf(SearchForFood("")))
    }
}