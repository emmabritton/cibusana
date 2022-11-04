package app.emmabritton.cibusana.flow.common

import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command

class LogOutUser : Command {
    override fun run(actionReceiver: ActionReceiver) {
        //clear persisted token, etc
    }
}