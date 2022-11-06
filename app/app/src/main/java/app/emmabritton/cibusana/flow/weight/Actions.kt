package app.emmabritton.cibusana.flow.weight

import app.emmabritton.system.Action
import java.time.ZonedDateTime

private val any = { _: WeightState? -> true }
private val viewingOnly = { state: WeightState? -> state is WeightState.Viewing }
private val loadingOnly = { state: WeightState? -> state is WeightState.Loading }

sealed class WeightAction(val stateValidator: (WeightState?) -> Boolean) : Action {
    object Show : WeightAction(any)
    class UserChangedStartDate(val date: ZonedDateTime) : WeightAction(viewingOnly)
    class UserChangedEndDate(val date: ZonedDateTime) : WeightAction(viewingOnly)
    class ReplaceWeight(val weights: Map<ZonedDateTime, Float>) : WeightAction(loadingOnly)
    class SearchRejected(val errors: List<Int>) : WeightAction(loadingOnly)
}