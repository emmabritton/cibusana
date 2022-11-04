package app.emmabritton.cibusana.flow.welcome

import app.emmabritton.system.Action

sealed class WelcomeAction : Action {
    object Show : WelcomeAction()
    object UserPressedLogin : WelcomeAction()
    object UserPressedRegister : WelcomeAction()
}