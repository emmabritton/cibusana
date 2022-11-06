package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

class GetWeightForRange(private val range: Range) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.getWeights(
            range.start.withHour(0).withMinute(0).withSecond(0),
            range.end.withHour(23).withMinute(59).withSecond(59)
        )
            .onSuccess {
                actionReceiver.receive(WeightAction.ReplaceWeight(it))
            }
            .onFailure {
                actionReceiver.receive(WeightAction.SearchRejected(it.errorCodes()))
            }
    }
}