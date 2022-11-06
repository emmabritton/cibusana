package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

class GetWeightForRange(private val range: Range) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.getWeights(range.start, range.end)
            .onSuccess {
                actionReceiver.receive(WeightAction.ReplaceWeight(it))
            }
            .onFailure {
                actionReceiver.receive(WeightAction.SearchRejected(it.errorCodes()))
            }
    }
}