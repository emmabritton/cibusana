package app.emmabritton.cibusana.flow.entry

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryRequest
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.network.models.MealResponse
import app.emmabritton.system.Action
import java.time.ZonedDateTime
import java.util.*

private val any = { _: EntryState? -> true }
private val viewingOnly = { state: EntryState? -> state is EntryState.Viewing }
private val loadingOnly = { state: EntryState? -> state is EntryState.Loading }

sealed class EntryAction(val stateValidator: (EntryState?) -> Boolean) : Action {
    object Show : EntryAction(any)
    class UserLoadedEntriesForDay(val date: ZonedDateTime) : EntryAction(loadingOnly)
    class ResultsFromServer(
        val food: Map<UUID, FoodResponse>,
        val meals: Map<UUID, MealResponse>,
        val entries: Map<String, List<MealEntryResponse>>
    ) : EntryAction(loadingOnly)
    object ServerError : EntryAction(loadingOnly)
    class UserDeletedEntry(val id: Int) : EntryAction(viewingOnly)
    class UserAddedEntry(val entry: MealEntryRequest) : EntryAction(viewingOnly)
    object UserSelectedNextDay : EntryAction(viewingOnly)
    object UserPreviousNextDay : EntryAction(viewingOnly)
}