package app.emmabritton.cibusana.internal.scopes

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.flow.welcome.WelcomeAction

class WelcomeScope(private val scope: RuntimeTest.RuntimeScope) {
        fun goToLogin() {
            scope.runtime.receive(WelcomeAction.UserPressedLogin)
        }

        fun goToRegister() {
            scope.runtime.receive(WelcomeAction.UserPressedRegister)
        }
    }