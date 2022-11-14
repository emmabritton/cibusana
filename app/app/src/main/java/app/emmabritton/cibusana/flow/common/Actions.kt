package app.emmabritton.cibusana.flow.common

import app.emmabritton.system.Action

sealed class CommonAction : Action {
    /**
     * A request failed with 120 (TOKEN_INVALID)
     */
    object ServerRejectedToken : CommonAction()

    /**
     * User wants to go to previous screen
     */
    object GoBack : CommonAction() {
        override fun describe() = "GoBack"
    }
}