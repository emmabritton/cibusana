package app.emmabritton.cibusana.flow.entry

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidState
import java.time.ZonedDateTime

fun reduceEntryAction(action: EntryAction, state: AppState): AppEffect {
    if (!action.stateValidator(state.uiState as? EntryState)) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}")
    }
    return when (action) {
        EntryAction.ServerError -> AppEffect(state.copy(uiState = (state.uiState as EntryState.Loading).toError()), emptyList())
        EntryAction.Show -> AppEffect(state.copy(uiState = EntryState.Loading.init()), listOf(GetEntriesForDay(ZonedDateTime.now())))
        is EntryAction.UserAddedEntry -> TODO()
        is EntryAction.UserDeletedEntry -> TODO()
        is EntryAction.UserLoadedEntriesForDay -> TODO()
        EntryAction.UserPreviousNextDay -> {
            val uiState = (state.uiState as EntryState.Viewing)
            AppEffect(state.copy(uiState = uiState.copy(date = uiState.date.minusDays(1))), emptyList())
        }
        EntryAction.UserSelectedNextDay -> {
            val uiState = (state.uiState as EntryState.Viewing)
            AppEffect(state.copy(uiState = uiState.copy(date = uiState.date.plusDays(1))), emptyList())
        }
        is EntryAction.ResultsFromServer -> {
            val loading = (state.uiState as EntryState.Loading)
            AppEffect(state.copy(uiState = EntryState.Viewing(action.food, action.meals, action.entries, loading.viewing.date)), emptyList())
        }
    }
}