package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

/**
 * Clear user data from storage
 */
class LogOutUser : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.user = null
    }
}

