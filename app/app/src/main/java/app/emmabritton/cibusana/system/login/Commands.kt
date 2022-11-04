package app.emmabritton.cibusana.system.login

import app.emmabritton.cibusana.data.UserController
import app.emmabritton.cibusana.errorCodes
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

class SubmitUserLogin(private val email: String, private val password: String) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val result = userController.login(email, password)

        result.onSuccess {
            actionReceiver.receive(LoginAction.Accepted(it.name, it.token))
        }

        result.onFailure { ex->
            actionReceiver.receive(LoginAction.Rejected(ex.errorCodes()))
        }
    }
}