package app.emmabritton.cibusana.flow.home

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.MealEntryResponse
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.FoodController
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.withEndOfDay
import app.emmabritton.cibusana.withStartOfDay
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.*

class GetEntriesForDay(val date: ZonedDateTime) : Command {
    private val foodController: FoodController by inject(FoodController::class.java)
    private val userController: UserController by inject(UserController::class.java)
    private val dataController: DataController by inject(DataController::class.java)

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
                actionReceiver.receive(HomeAction.ServerErrorOccurred)
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
                    actionReceiver.receive(HomeAction.ServerErrorOccurred)
                    return
                }

                if (foodResults == null) {
                    Timber.e("GetEntriesForDay foodResults == null")
                    actionReceiver.receive(HomeAction.ServerErrorOccurred)
                    return
                }
                val food: Map<UUID, FoodResponse> = foodResults.associateBy { it.id }
                val meals: Map<String, MutableList<MealEntryResponse>> =
                    mealTimes.associateWith { mutableListOf() }

                for (entry in entries) {
                    meals[entry.mealTime]?.add(entry)
                }

                actionReceiver.receive(HomeAction.ResultsFromServer(food, emptyMap(), meals))
            }
    }

}