package app.emmabritton.cibusana.flow.measurements

import app.emmabritton.cibusana.DateRange
import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.flow.weight.WeightAction
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.withEndOfDay
import app.emmabritton.cibusana.withStartOfDay
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

class GetMeasurementForRange(private val range: DateRange) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.getMeasurements(
            range.start.withStartOfDay(),
            range.end.withEndOfDay()
        )
            .onSuccess { list ->
                actionReceiver.receive(MeasurementAction.SetData(list.getOrNull(0)))
            }
            .onFailure {
                actionReceiver.receive(WeightAction.SearchRejected(it.errorCodes()))
            }
    }
}
