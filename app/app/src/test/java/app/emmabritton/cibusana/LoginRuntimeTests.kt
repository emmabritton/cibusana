package app.emmabritton.cibusana

import app.emmabritton.cibusana.data.DI_URL
import app.emmabritton.cibusana.data.dataModule
import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.cibusana.system.login.UserSubmittedLogin
import app.emmabritton.cibusana.system.login.UserUpdatedLoginEmail
import app.emmabritton.cibusana.system.login.UserUpdatedLoginPassword
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
import kotlin.test.assertEquals

class LoginRuntimeTests : KoinTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        stopKoin()
        server.shutdown()
    }

    @Test
    fun `check login with invalid details`() {
        server.enqueue(MockResponse().setBody("""{"error":{"error_codes":[103],"error_message":"invalid"}}"""))

        startKoin {
            modules(module {
                single(named(DI_URL)) {
                    server.url("/").toString()
                }
            }, testModule, dataModule)
        }

        val runtime = createTestRuntime()

        runtime.receive(UserUpdatedLoginEmail("test@test.com"))
        runtime.receive(UserUpdatedLoginPassword("testtest"))

        runtime.receive(UserSubmittedLogin)

        runtime.assertNoGlobalError()

        val error = runtime.assertLoginState(LoginState.Error::class.java)
        assertEquals(error.message, "103")
    }

    @Test
    fun `check login with valid details`() {
        server.enqueue(MockResponse().setBody("""{"content":{"name":"Test","token":"token-value"}}"""))

        startKoin {
            modules(module {
                single(named(DI_URL)) {
                    server.url("/").toString()
                }
            }, testModule, dataModule)
        }

        val runtime = createTestRuntime()

        runtime.receive(UserUpdatedLoginEmail("test@test.com"))
        runtime.receive(UserUpdatedLoginPassword("testtest"))

        runtime.receive(UserSubmittedLogin)

        runtime.assertNoGlobalError()

        val loggedIn = runtime.assertLoginState(LoginState.LoggedIn::class.java)
        assertEquals(loggedIn.name, "Test")
        assertEquals(loggedIn.token, "token-value")
    }
}