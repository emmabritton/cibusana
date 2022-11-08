package app.emmabritton.cibusana.reducer

import app.emmabritton.cibusana.assertUiState
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.reduce
import app.emmabritton.cibusana.flow.register.RegisterState
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import org.junit.Test

class WelcomeTests {
    @Test
    fun `check Welcome login button`() {
        val state = AppState.init().copy(uiState = WelcomeState)

        val effect = reduce(WelcomeAction.UserPressedLogin, state)
        effect.newState.assertUiState(LoginState.Entering::class.java)
    }

    @Test
    fun `check Welcome register button`() {
        val state = AppState.init().copy(uiState = WelcomeState)

        val effect = reduce(WelcomeAction.UserPressedRegister, state)
        effect.newState.assertUiState(RegisterState.Entering::class.java)
    }
}