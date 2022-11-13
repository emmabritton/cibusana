package app.emmabritton.cibusana.flow.measurements

import app.emmabritton.cibusana.flow.weight.WeightState
import app.emmabritton.cibusana.network.models.MeasurementResponse
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig

sealed class MeasurementState(override val config: UiStateConfig) : UiState {
    object Loading : MeasurementState(UiStateConfig.loadingScreen()) {
        fun init() = WeightState.Loading(
            WeightState.Viewing.init()
        )
    }

    object Error : MeasurementState(UiStateConfig.tempScreen())

    class Viewing(val data: MeasurementResponse?) :
        MeasurementState(UiStateConfig.generalScreen()) {
        fun toLoading() = WeightState.Loading
    }
}