package app.emmabritton.cibusana.flow.measurements

import app.emmabritton.cibusana.network.models.MeasurementResponse
import app.emmabritton.system.Action

private val any = { _: MeasurementState? -> true }
private val viewingOnly = { state: MeasurementState? -> state is MeasurementState.Viewing }
private val loadingOnly = { state: MeasurementState? -> state is MeasurementState.Loading }

sealed class MeasurementAction(val stateValidator: (MeasurementState?) -> Boolean) : Action {
    object Show : MeasurementAction(any)
    class SetData(val measurements: MeasurementResponse?) : MeasurementAction(loadingOnly)
    object Error : MeasurementAction(loadingOnly)
}