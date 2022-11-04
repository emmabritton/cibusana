package app.emmabritton.cibusana.system.welcome

import app.emmabritton.system.Action

sealed class WelcomeAction : Action {
    object UserPressedLogin : WelcomeAction()
    object UserPressedRegister : WelcomeAction()
}