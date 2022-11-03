package app.emmabritton.cibusana

import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.cibusana.system.login.UserUpdatedLoginEmail
import app.emmabritton.cibusana.system.reduce
import org.junit.Test
import kotlin.test.assertEquals

class LoginReduceTests {
    @Test
    fun `check init state is Entering with no text`() {
        val state = AppState.init()

        state.assertNoGlobalError()

        val entering = state.assertLoginState(LoginState.Entering::class.java)
        assertEquals("", entering.email, "Email should be empty at start of app")
        assertEquals("", entering.password, "Password should be empty at start of app")
    }

    @Test
    fun `check entering email updates state`() {
        val state = AppState.init()

        val effect = reduce(UserUpdatedLoginEmail("t"), state)

        effect.newState.assertNoGlobalError()

        val entering = effect.newState.assertLoginState(LoginState.Entering::class.java)
        assertEquals("t", entering.email, "Email should now contain the letter")
        assertEquals("", entering.password, "Password should still be empty")
    }
}