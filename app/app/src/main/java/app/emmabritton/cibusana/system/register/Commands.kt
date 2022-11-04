package app.emmabritton.cibusana.system.register

import app.emmabritton.cibusana.data.UserController
import app.emmabritton.cibusana.errorCodes
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

class SubmitUserRegister(private val email: String, private val password: String, private val name: String) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val result = userController.register(email, password, name)

        result.onSuccess {
            actionReceiver.receive(RegisterAction.Accepted(name, it.token))
        }

        result.onFailure { ex->
            actionReceiver.receive(RegisterAction.Rejected(ex.errorCodes()))
        }
    }
}