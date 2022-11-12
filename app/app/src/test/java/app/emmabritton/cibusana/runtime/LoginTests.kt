package app.emmabritton.cibusana.runtime

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.internal.Home
import app.emmabritton.cibusana.internal.Login
import app.emmabritton.cibusana.internal.Splash
import app.emmabritton.cibusana.internal.Welcome
import app.emmabritton.cibusana.persist.models.User
import org.junit.Test
import java.util.*

class LoginTests : RuntimeTest() {

    @Test
    fun `check login with invalid details`() {
        app {
            skipTo(Login) {
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
            skipTo(Login) {
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
            verifyAt(Splash) {
                init()
            }
            verifyAt(Welcome) {
                goToLogin()
            }
            verifyAt(Login) {
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