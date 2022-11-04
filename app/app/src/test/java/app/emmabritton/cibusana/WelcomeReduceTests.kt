package app.emmabritton.cibusana

import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.system.reduce
import app.emmabritton.cibusana.flow.register.RegisterState
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import org.junit.Test

class WelcomeReduceTests {
    @Test
    fun `check Welcome login button`() {
        val state = AppState.init()

        state.assertNoGlobalError()

        state.assertUiState(WelcomeState::class.java)

        val effect = reduce(WelcomeAction.UserPressedLogin, state)

        effect.newState.assertUiState(LoginState.Entering::class.java)
    }

    @Test
    fun `check Welcome register button`() {
        val state = AppState.init()

        state.assertNoGlobalError()

        state.assertUiState(WelcomeState::class.java)

        val effect = reduce(WelcomeAction.UserPressedRegister, state)

        effect.newState.assertUiState(RegisterState.Entering::class.java)
    }
}