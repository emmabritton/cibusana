package app.emmabritton.cibusana

import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.network.DI_URL
import app.emmabritton.cibusana.network.networkModule
import app.emmabritton.cibusana.persist.Prefs
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.cibusana.system.AppState
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertEquals

class LoginRuntimeTests : KoinTest {
    private lateinit var server: MockWebServer

    private class MemoryPrefs : Prefs {
        override var user: User? = null
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        startKoin {
            modules(module {
                single(named(DI_URL)) {
                    server.url("/").toString()
                }
            }, testModule, networkModule, module {
                single {
                    @Suppress("USELESS_CAST")
                    MemoryPrefs() as Prefs
                }
                single {
                    UserController(get(), get())
                }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        server.shutdown()
    }

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

        val prefs: Prefs = get()
        assertEquals(prefs.user, null)
    }

    @Test
    fun `check login with valid details`() {
        server.enqueue(MockResponse().setBody("""{"content":{"name":"Test","token":"token-value"}}"""))

        val runtime = createTestRuntime()

        runtime.state = AppState.init().copy(uiState = LoginState.Entering.init())

        runtime.receive(LoginAction.UserUpdatedEmail("test@test.com"))
        runtime.receive(LoginAction.UserUpdatedPassword("testtest"))

        runtime.receive(LoginAction.UserSubmitted)

        runtime.assertNoGlobalError()

        runtime.assertUiState(HomeState::class.java)
        assertEquals(runtime.state.user?.name, "Test")
        assertEquals(runtime.state.user?.token, "token-value")

        val prefs: Prefs = get()
        assertEquals(prefs.user, User("Test", "token-value"))
    }
}