package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.DateRange
import app.emmabritton.cibusana.network.models.WeightResponse
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
            val viewing = loading.toViewing().copy(weights = action.weights)
            AppEffect(
                state.copy(uiState = viewing),
                emptyList()
            )
        }
        is WeightAction.SearchRejected -> AppEffect(
            state.copy(uiState = WeightState.Error),
            emptyList()
        )
        WeightAction.Show -> AppEffect(
            state.copy(uiState = WeightState.Loading.init()),
            listOf(GetWeightForRange(DateRange.init()))
        )
        is WeightAction.UserChangedStartDate -> {
            val oldState = (state.uiState as WeightState.Viewing)
            val newRange = oldState.range.copy(start = action.date)
            val viewing = oldState.copy(range = newRange)
            AppEffect(
                state.copy(uiState = viewing.toLoading()),
                listOf(GetWeightForRange(newRange))
            )
        }
        is WeightAction.UserChangedEndDate -> {
            val oldState = (state.uiState as WeightState.Viewing)
            val newRange = oldState.range.copy(end = action.date)
            val viewing = oldState.copy(range = newRange)
            AppEffect(
                state.copy(uiState = viewing.toLoading()),
                listOf(GetWeightForRange(newRange))
            )
        }
        WeightAction.SubmitAmount -> {
            val oldState = (state.uiState as WeightState.Viewing)
            AppEffect(
                state.copy(uiState = oldState.toLoading()),
                listOf(SubmitWeight(oldState.newAmount, oldState.newDate))
            )
        }
        is WeightAction.UserChangedAmount -> {
            val uiState =
                (state.uiState as WeightState.Viewing).copy(newAmount = action.amount.toInt())
            AppEffect(state.copy(uiState = uiState), emptyList())
        }
        WeightAction.SubmitSuccess -> {
            val viewing = (state.uiState as WeightState.Loading).toViewing()
            val weights = viewing.weights.toMutableList()
            weights.add(WeightResponse(viewing.newAmount, viewing.newDate))
            AppEffect(
                state.copy(uiState = viewing.copy(weights = weights)),
                emptyList()
            )
        }
        is WeightAction.SubmitWeightRejected -> AppEffect(
            state.copy(uiState = WeightState.Error),
            emptyList()
        )
    }
}