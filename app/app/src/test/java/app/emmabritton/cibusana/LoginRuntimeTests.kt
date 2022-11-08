package app.emmabritton.cibusana

import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.splash.SplashAction
import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.persist.Prefs
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.cibusana.system.AppState
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LoginRuntimeTests : RuntimeTest() {
    @Test
    fun `check login with invalid details`() {
        server.enqueue(MockResponse().setBody("""{"error":{"error_codes":[103],"error_message":"invalid"}}"""))

        val runtime = createTestRuntime()
        runtime.state = AppState.init().copy(uiState = LoginState.Entering.init())

        runtime.receive(LoginAction.UserUpdatedEmail("test@test.com"))
        runtime.receive(LoginAction.UserUpdatedPassword("testtest"))

        runtime.receive(LoginAction.UserSubmitted)

        runtime.assertNoGlobalError()

        val error = runtime.assertUiState(LoginState.Error::class.java)
        assertEquals(error.message, "103")

        val prefs: Prefs by inject(Prefs::class.java)
        assertEquals(prefs.user, null)
    }

    @Test
    fun `check login with valid details`() {
        val uuid = UUID.randomUUID()
        server.enqueue(MockResponse().setBody("""{"content":{"name":"Test","token":"${uuid.toString()}"}}"""))

        val runtime = createTestRuntime()

        runtime.state = AppState.init().copy(uiState = LoginState.Entering.init())

        runtime.receive(LoginAction.UserUpdatedEmail("test@test.com"))
        runtime.receive(LoginAction.UserUpdatedPassword("testtest"))

        runtime.receive(LoginAction.UserSubmitted)

        runtime.assertNoGlobalError()

        runtime.assertUiState(HomeState::class.java)
        assertEquals(runtime.state.user?.name, "Test")
        assertEquals(runtime.state.user?.token, uuid)

        val prefs: Prefs by inject(Prefs::class.java)
        assertEquals(prefs.user, User("Test", uuid))
    }

    @Test
    fun `test whole app flow from splash to home, with one failed login`() {
        setupPreloadData()
        server.enqueue(MockResponse().setBody("""{"content":{"name":"Test2","token":"${UUID.randomUUID()}"}}"""))

        val runtime = createTestRuntime()

        runtime.assertUiState(SplashState::class.java)
        runtime.receive(SplashAction.InitialiseApp)

        runtime.assertUiState(WelcomeState::class.java)
        runtime.receive(WelcomeAction.UserPressedLogin)

        runtime.assertUiState(LoginState.Entering::class.java)
        runtime.receive(LoginAction.UserUpdatedEmail("test"))
        runtime.receive(LoginAction.UserUpdatedPassword("test"))
        runtime.receive(LoginAction.UserSubmitted)

        runtime.assertUiState(HomeState::class.java)
        assertNotNull(runtime.state.user)
        assertEquals(runtime.state.user!!.name, "Test2")
    }
}