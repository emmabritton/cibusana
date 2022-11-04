package app.emmabritton.cibusana.flow.common

import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent

class LogOutUser : Command {
    private val userController: UserController by KoinJavaComponent.inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        userController.user = null
    }
}

class LoadUserData : Command {
    private val userController: UserController by KoinJavaComponent.inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val user = userController.user
        if (user != null) {
            actionReceiver.receive(CommonAction.LoggedIn(user))
        } else {
            actionReceiver.receive(WelcomeAction.Show)
        }
    }
}