package app.emmabritton.cibusana.flow.welcome

import app.emmabritton.system.Action

sealed class WelcomeAction : Action {
    /**
     * The welcome screen should be shown
     */
    object Show : WelcomeAction()
    /**
     * User pressed the login button on the welcome screen
     */
    object UserPressedLogin : WelcomeAction()
    /**
     * User pressed the register button on the welcome screen
     */
    object UserPressedRegister : WelcomeAction()
}