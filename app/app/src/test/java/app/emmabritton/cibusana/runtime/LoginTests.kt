package app.emmabritton.cibusana.runtime

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.ScopeType
import app.emmabritton.cibusana.ScopeType.SKIP_TO
import app.emmabritton.cibusana.ScopeType.VERIFY
import app.emmabritton.cibusana.persist.models.User
import org.junit.Test
import java.util.*

class LoginTests : RuntimeTest() {

    @Test
    fun `check login with invalid details`() {
        app {
            login(SKIP_TO) {
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
            login(SKIP_TO) {
                enterValidDetailsAndSubmit(testUser)
            }
            home(VERIFY) {
                assertLoggedIn(testUser)
            }
        }
    }

    @Test
    fun `test whole app flow from splash to home, with one failed login`() {
        val testUser = User("Test2", UUID.randomUUID())
        app {
            splash(VERIFY) {
                init()
            }
            welcome(VERIFY) {
                goToLogin()
            }
            login(VERIFY) {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible()
                clearError()

                enterValidDetailsAndSubmit(testUser)
            }
            home(VERIFY) {
                assertLoggedIn(testUser)
            }
        }
    }
}