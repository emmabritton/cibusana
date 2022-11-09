package app.emmabritton.cibusana.flow.weight

import app.emmabritton.cibusana.network.models.WeightResponse
import app.emmabritton.system.Action
import java.time.ZonedDateTime

private val any = { _: WeightState? -> true }
private val viewingOnly = { state: WeightState? -> state is WeightState.Viewing }
private val loadingOnly = { state: WeightState? -> state is WeightState.Loading }

sealed class WeightAction(val stateValidator: (WeightState?) -> Boolean) : Action {
    object Show : WeightAction(any)
    object SubmitSuccess : WeightAction(loadingOnly)
    object SubmitAmount : WeightAction(viewingOnly)
    class UserChangedAmount(val amount: String): WeightAction(viewingOnly)
    class UserChangedStartDate(val date: ZonedDateTime) : WeightAction(viewingOnly)
    class UserChangedEndDate(val date: ZonedDateTime) : WeightAction(viewingOnly)
    class ReplaceWeight(val weights: List<WeightResponse>) : WeightAction(loadingOnly)
    class SearchRejected(val errors: List<Int>) : WeightAction(loadingOnly)
    class SubmitWeightRejected(val errors: List<Int>) : WeightAction(loadingOnly)
}