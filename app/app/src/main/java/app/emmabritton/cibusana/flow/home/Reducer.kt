package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidState
import java.time.ZonedDateTime

fun reduceHomeAction(action: HomeAction, state: AppState): AppEffect {
    if (!action.stateValidator(state.uiState as? HomeState)) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}")
    }
    return when (action) {
        is HomeAction.UserLoggedIn -> AppEffect(
            state.copy(
                user = action.user,
                uiState = HomeState.Loading(ZonedDateTime.now())
            ), listOf(GetEntriesForDay(ZonedDateTime.now()))
        )
        HomeAction.ServerErrorOccurred -> AppEffect(
            state.copy(uiState = (state.uiState as HomeState.Loading).toError()),
            emptyList()
        )
        HomeAction.ShowToday -> AppEffect(
            state.copy(uiState = HomeState.Loading(ZonedDateTime.now())),
            listOf(GetEntriesForDay(ZonedDateTime.now()))
        )
        is HomeAction.ShowDay -> AppEffect(
            state.copy(uiState = HomeState.Loading(action.date)),
            listOf(GetEntriesForDay(action.date))
        )
        HomeAction.UserPressedPrevDay -> {
            val uiState = (state.uiState as HomeState.Viewing)
            val newState = HomeState.Loading(uiState.date.minusDays(1))
            AppEffect(state.copy(uiState = newState), listOf(GetEntriesForDay(newState.date)))
        }
        HomeAction.UserPressedNextDay -> {
            val uiState = (state.uiState as HomeState.Viewing)
            val newState = HomeState.Loading(uiState.date.plusDays(1))
            AppEffect(state.copy(uiState = newState), listOf(GetEntriesForDay(newState.date)))
        }
        is HomeAction.ResultsFromServer -> {
            val loading = (state.uiState as HomeState.Loading)
            val newState = HomeState.Viewing(loading.date, action.entries, action.food)
            AppEffect(state.copy(uiState = newState), emptyList())
        }
    }
}