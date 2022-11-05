package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.flow.common.CommonAction
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

/**
 * Send name, email and password to server to register
 */
class SubmitUserRegister(private val email: String, private val password: String, private val name: String) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val result = userController.register(email, password, name)

        result.onSuccess {
            val user = User(name, it.token)
            userController.user = user
            actionReceiver.receive(CommonAction.LoggedIn(user))
        }

        result.onFailure { ex->
            actionReceiver.receive(RegisterAction.Rejected(ex.errorCodes()))
        }
    }
}