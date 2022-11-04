package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.Action

sealed class CommonAction : Action {
    object ServerRejectedToken : CommonAction()
    class LoggedIn(val user: User) : CommonAction()
    object InitialiseApp : CommonAction()
}