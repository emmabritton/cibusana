package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidState

fun reduceWeightAction(action: WeightAction, state: AppState): AppEffect {
    if (!action.stateValidator(state.uiState as? WeightState)) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}")
    }
    return when (action) {
        is WeightAction.ReplaceWeight -> {
            val loading = (state.uiState as WeightState.Loading)
            AppEffect(state.copy(uiState = WeightState.Viewing(action.weights, loading.range)), emptyList())
        }
        is WeightAction.SearchRejected -> AppEffect(state.copy(uiState = WeightState.Error), emptyList())
        WeightAction.Show -> AppEffect(
            state.copy(uiState = WeightState.Viewing.init()),
            listOf(GetWeightForRange(Range.init()))
        )
        is WeightAction.UserChangedStartDate -> {
            val newRange = (state.uiState as WeightState.Viewing).range.copy(start = action.date)
            AppEffect(state.copy(uiState = WeightState.Loading(newRange)), listOf(GetWeightForRange(newRange)))
        }
        is WeightAction.UserChangedEndDate -> {
            val newRange = (state.uiState as WeightState.Viewing).range.copy(end = action.date)
            AppEffect(state.copy(uiState = WeightState.Loading(newRange)), listOf(GetWeightForRange(newRange)))
        }
    }
}