package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.R
import app.emmabritton.cibusana.network.models.MeasurementUnit
import app.emmabritton.cibusana.system.TopBarConfig
import app.emmabritton.cibusana.system.UiState
import app.emmabritton.cibusana.system.UiStateConfig
import app.emmabritton.cibusana.system.loggedOutBarConfig
import java.time.ZonedDateTime

sealed class RegisterState(
    override val config: UiStateConfig,
    override val topBarConfig: TopBarConfig = loggedOutBarConfig(R.string.screen_register)
) : UiState {
    /**
     * Entering data into register screen
     */
    data class Entering(
        val email: String,
        val password: String,
        val name: String,
        val height: Int,
        val targetWeight: Int?,
        val targetDate: ZonedDateTime?,
        val units: MeasurementUnit
    ) : RegisterState(UiStateConfig.generalScreen()) {
        fun toLoading() = Loading(this)

        companion object {
            fun init(): Entering {
                return Entering("", "", "", 170, null, null, MeasurementUnit.Metric)
            }
        }
    }

    /**
     * Submitting register request to server
     */
    data class Loading(val details: Entering) : RegisterState(UiStateConfig.loadingScreen()) {
        fun toError(msg: String) = Error(msg, details)
    }

    /**
     * Something went wrong with register request
     */
    data class Error(val message: String, val details: Entering) :
        RegisterState(UiStateConfig.tempScreen()) {
        fun toEntering() = details
    }
}