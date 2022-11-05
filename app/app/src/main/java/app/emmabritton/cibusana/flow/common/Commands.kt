package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

/**
 * Clear user data from storage
 */
class LogOutUser : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.user = null
    }
}

