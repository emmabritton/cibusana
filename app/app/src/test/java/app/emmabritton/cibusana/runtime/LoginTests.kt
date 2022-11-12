package app.emmabritton.cibusana.runtime

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.internal.Home
import app.emmabritton.cibusana.internal.Login
import app.emmabritton.cibusana.internal.Splash
import app.emmabritton.cibusana.internal.Welcome
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.UiState
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class LoginTests : RuntimeTest() {

    private val skipHistory: List<UiState> = listOf(WelcomeState, LoginState.Entering.init())

    @Test
    fun `check full flow and skip result in same state`() {
        lateinit var skipState: AppState
        lateinit var flowState: AppState
        app {
            skipTo(Login, skipHistory) {
                skipState = this@app.runtime.state
            }
        }
        app {
            verifyAt(Splash) {
                init()
            }
            verifyAt(Welcome) {
                goToLogin()
            }
            verifyAt(Login) {
                flowState = this@app.runtime.state
            }
        }

        assertEquals(skipState, flowState)
    }

    @Test
    fun `check login with invalid details`() {
        app {
            skipTo(Login, skipHistory) {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible("103")
                assertNotLoggedIn()
            }
        }
    }

    @Test
    fun `check login with valid details`() {
        val testUser = User("Test", UUID.randomUUID())
        app {
            skipTo(Login, skipHistory) {
                enterValidDetailsAndSubmit(testUser)
            }
            verifyAt(Home) {
                assertLoggedIn(testUser)
            }
        }
    }

    @Test
    fun `test whole app flow from splash to home, with one failed login`() {
        val testUser = User("Test2", UUID.randomUUID())
        app {
            skipTo(Login, skipHistory) {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible()
                clearError()

                enterValidDetailsAndSubmit(testUser)
            }
            verifyAt(Home) {
                assertLoggedIn(testUser)
            }
        }
    }
}