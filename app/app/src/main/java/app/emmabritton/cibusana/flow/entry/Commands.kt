package app.emmabritton.cibusana.flow.entry

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.FoodController
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.withEndOfDay
import app.emmabritton.cibusana.withStartOfDay
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.UUID

class GetEntriesForDay(val date: ZonedDateTime) : Command {
    private val foodController: FoodController by KoinJavaComponent.inject(FoodController::class.java)
    private val userController: UserController by KoinJavaComponent.inject(UserController::class.java)
    private val dataController: DataController by KoinJavaComponent.inject(DataController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        /**
         * Get a list of entries for a day for a user
         * Get a list of food/meals referenced in the entries
         * Get a list of valid meal times
         * Convert list into map of meal time to food/meal
         */
        userController.getEntries(date.withStartOfDay(), date.withEndOfDay())
            .onFailure {
                Timber.e(it, "GetEntriesForDay getEntry")
                actionReceiver.receive(EntryAction.ServerError)
            }
            .onSuccess { entries ->
                val foodResults: List<FoodResponse>? =
                    entries.map { foodController.exact(it.foodId) }
                        .fold(mutableListOf<FoodResponse>() as MutableList?) { acc, result ->
                            if (acc != null) {
                                val food = result.getOrNull()
                                if (food != null) {
                                    acc.add(food)
                                    return@fold acc
                                } else {
                                    Timber.e(result.exceptionOrNull(), "Failed to get food")
                                }
                            }
                            null
                        }

                val mealTimes = dataController.getMealTimes().getOrNull()
                if (mealTimes == null) {
                    Timber.e("GetEntriesForDay mealTimes == null")
                    actionReceiver.receive(EntryAction.ServerError)
                    return
                }

                if (foodResults == null) {
                    Timber.e("GetEntriesForDay foodResults == null")
                    actionReceiver.receive(EntryAction.ServerError)
                    return
                }
                val food: Map<UUID, FoodResponse> = foodResults.associateBy { it.id }
                val meals: Map<String, MutableList<MealEntryResponse>> =
                    mealTimes.associateWith { mutableListOf() }

                for (entry in entries) {
                    meals[entry.mealTime]?.add(entry)
                }

                actionReceiver.receive(EntryAction.ResultsFromServer(food, emptyMap(), meals))
            }
    }

}