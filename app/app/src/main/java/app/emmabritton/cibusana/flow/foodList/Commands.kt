package app.emmabritton.cibusana.flow.foodList

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.persist.FoodController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

/**
 * Clear user data from storage
 */
class SearchForFood(private val searchTerm: String) : Command {
    private val foodController: FoodController by inject(FoodController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        foodController.search(searchTerm, 0)
            .onSuccess {
                actionReceiver.receive(FoodAction.ReplaceFood(it.second))
            }
            .onFailure {
                actionReceiver.receive(FoodAction.SearchRejected(it.errorCodes()))
            }
    }
}

