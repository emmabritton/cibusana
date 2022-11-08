package app.emmabritton.cibusana.runtime

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.persist.models.User
import org.junit.Test
import java.util.*

class LoginTests : RuntimeTest() {

    @Test
    fun `check login with invalid details`() {
        app {
            login(true) {
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
            login(true) {
                enterValidDetailsAndSubmit(testUser)
            }
            home {
                assertLoggedIn(testUser)
            }
        }
    }

    @Test
    fun `test whole app flow from splash to home, with one failed login`() {
        val testUser = User("Test2", UUID.randomUUID())
        app {
            splash {
                init()
            }
            welcome {
                goToLogin()
            }
            login {
                enterInvalidDetailsAndSubmit()
                assertErrorVisible()
                clearError()

                enterValidDetailsAndSubmit(testUser)
            }
            home {
                assertLoggedIn(testUser)
            }
        }
    }
}