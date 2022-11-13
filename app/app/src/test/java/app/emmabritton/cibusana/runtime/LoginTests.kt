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

    private val uiHistory: List<UiState> = listOf(WelcomeState, LoginState.Entering.init())

    @Test
    fun `GIVEN app started WHEN splash-init, welcome-login THEN at login with same history`() {
        lateinit var skipState: AppState
        lateinit var flowState: AppState
        app {
            skipTo(Login, uiHistory) {
                skipState = this@app.runtime.state
            }
        }
        app {
            assertAt(Splash) {
                init()
            }
            assertAt(Welcome) {
                goToLogin()
            }
            assertAt(Login) {
                flowState = this@app.runtime.state
            }
        }

        assertEquals(skipState, flowState)
    }

    @Test
    fun `GIVEN on login WHEN invalid login THEN not logged in and on login error`() {
        app {
            skipTo(Login, uiHistory) {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible("103")
                assertNotLoggedIn()
            }
        }
    }

    @Test
    fun `GIVEN on login WHEN valid login THEN logged in and at home`() {
        val testUser = User("Test", UUID.randomUUID())
        app {
            skipTo(Login, uiHistory) {
                enterValidDetailsAndSubmit(testUser)
            }
            assertAt(Home) {
                assertLoggedIn(testUser)
            }
        }
    }

    @Test
    fun `GIVEN on login WHEN invalid then valid login THEN logged in and at home`() {
        val testUser = User("Test2", UUID.randomUUID())
        app {
            skipTo(Login, uiHistory) {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible()
                clearError()

                enterValidDetailsAndSubmit(testUser)
            }
            assertAt(Home) {
                assertLoggedIn(testUser)
            }
        }
    }
}