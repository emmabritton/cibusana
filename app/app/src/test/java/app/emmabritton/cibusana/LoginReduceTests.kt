package app.emmabritton.cibusana

import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.reduce
import org.junit.Test
import kotlin.test.assertEquals

class LoginReduceTests {
    @Test
    fun `check init Entering state has no text`() {
        val state = AppState.init().copy(uiState = LoginState.Entering.init())

        val entering = state.assertUiState(LoginState.Entering::class.java)
        assertEquals("", entering.email, "Email should be empty at start of app")
        assertEquals("", entering.password, "Password should be empty at start of app")
    }

    @Test
    fun `check entering email updates state`() {
        val state = AppState.init().copy(uiState = LoginState.Entering.init())

        val effect = reduce(LoginAction.UserUpdatedEmail("t"), state)

        val entering = effect.newState.assertUiState(LoginState.Entering::class.java)
        assertEquals("t", entering.email, "Email should now contain the letter")
        assertEquals("", entering.password, "Password should still be empty")
    }
}