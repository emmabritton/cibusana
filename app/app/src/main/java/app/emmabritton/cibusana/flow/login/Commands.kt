package app.emmabritton.cibusana.flow.login

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.flow.common.CommonAction
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

/**
 * Send email and password to server to login
 */
class SubmitUserLogin(private val email: String, private val password: String) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val result = userController.login(email, password)

        result.onSuccess {
            val user = User(it.name, it.token)
            userController.user = user
            actionReceiver.receive(CommonAction.LoggedIn(user))
        }

        result.onFailure { ex->
            actionReceiver.receive(LoginAction.Rejected(ex.errorCodes()))
        }
    }
}