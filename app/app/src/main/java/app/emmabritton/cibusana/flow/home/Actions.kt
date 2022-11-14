package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.network.models.MealResponse
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.Action
import java.time.ZonedDateTime
import java.util.*

private val any = { _: HomeState? -> true }
private val viewingOnly = { state: HomeState? -> state is HomeState.Viewing }
private val loadingOnly = { state: HomeState? -> state is HomeState.Loading }

sealed class HomeAction(val stateValidator: (HomeState?) -> Boolean) : Action {
    class UserLoggedIn(val user: User) : HomeAction(any)
    object ShowToday : HomeAction(any)
    class ShowDay(val date: ZonedDateTime) : HomeAction(any)
    object ServerErrorOccurred : HomeAction(loadingOnly)
    class ResultsFromServer(
        val food: Map<UUID, FoodResponse>,
        val meals: Map<UUID, MealResponse>,
        val entries: Map<String, MutableList<MealEntryResponse>>
    ) : HomeAction(loadingOnly)

    object UserPressedNextDay : HomeAction(viewingOnly)
    object UserPressedPrevDay : HomeAction(viewingOnly)
}