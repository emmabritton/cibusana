package app.emmabritton.cibusana.flow.measurements

import app.emmabritton.cibusana.DateRange
import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import java.time.ZonedDateTime

fun reduceMeasurementAction(action: MeasurementAction, state: AppState): AppEffect {
    return when (action) {
        MeasurementAction.Error -> AppEffect(
            state.copy(uiState = MeasurementState.Error),
            emptyList()
        )
        MeasurementAction.Show -> AppEffect(
            state.copy(uiState = MeasurementState.Loading), listOf(
                GetMeasurementForRange(
                    DateRange(ZonedDateTime.now(), ZonedDateTime.now())
                )
            )
        )
        is MeasurementAction.SetData -> AppEffect(
            state.copy(uiState = MeasurementState.Viewing(action.measurements)), emptyList()
        )
    }
}