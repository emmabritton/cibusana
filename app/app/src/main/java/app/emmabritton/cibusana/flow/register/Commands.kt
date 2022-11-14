package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.errorCodes
import app.emmabritton.cibusana.flow.home.HomeAction
import app.emmabritton.cibusana.network.models.RegisterRequest
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent.inject

/**
 * Send name, email and password to server to register
 */
class SubmitUserRegister(private val request: RegisterRequest) : Command {
    private val userController: UserController by inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val result = userController.register(request)

        result.onSuccess {
            val user = User(request.name, it.token)
            userController.user = user
            actionReceiver.receive(HomeAction.UserLoggedIn(user))
        }

        result.onFailure { ex->
            actionReceiver.receive(RegisterAction.Rejected(ex.errorCodes()))
        }
    }
}