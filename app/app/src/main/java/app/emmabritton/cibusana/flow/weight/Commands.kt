package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.DateRange
import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.withEndOfDay
import app.emmabritton.cibusana.withStartOfDay
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

class GetWeightForRange(private val range: DateRange) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.getWeights(
            range.start.withStartOfDay(),
            range.end.withEndOfDay()
        )
            .onSuccess { list ->
                actionReceiver.receive(WeightAction.ReplaceWeight(list.sortedBy { it.date }))
            }
            .onFailure {
                actionReceiver.receive(WeightAction.SearchRejected(it.errorCodes()))
            }
    }
}

class SubmitWeight(private val grams: Int, private val date: ZonedDateTime) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.addWeight(grams, date)
            .onSuccess {
                actionReceiver.receive(WeightAction.SubmitSuccess)
            }
            .onFailure {
                actionReceiver.receive(WeightAction.SubmitWeightRejected(it.errorCodes()))
            }
    }


}