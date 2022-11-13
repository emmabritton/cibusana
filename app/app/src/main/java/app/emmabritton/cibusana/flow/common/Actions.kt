package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.Action

sealed class CommonAction : Action {
    /**
     * A request failed with 120 (TOKEN_INVALID)
     */
    object ServerRejectedToken : CommonAction()

    /**
     * A login or register request successfully returned
     */
    class LoggedIn(val user: User) : CommonAction()

    /**
     * User wants to go to previous screen
     */
    object GoBack : CommonAction() {
        override fun describe() = "GoBack"
    }

    /**
     * User pressed a home button (such as SmallLogo)
     */
    object UserPressedHome: CommonAction()
}