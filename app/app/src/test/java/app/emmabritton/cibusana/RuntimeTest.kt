package app.emmabritton.cibusana

import app.emmabritton.cibusana.LoginResponse
import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.splash.SplashAction
import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.network.DI_URL
import app.emmabritton.cibusana.network.networkModule
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.Prefs
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.UiState
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.*
import kotlin.test.assertEquals

typealias LoginResponse = String

open class RuntimeTest {
    protected lateinit var server: MockWebServer

    protected class MemoryPrefs : Prefs {
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
                single {
                    DataController(get(), get())
                }
            })
        }
    }

    fun queuePreloadData() {
        //has to match order in command PreLoadCacheData
        server.enqueue(MockResponse().setBody("""{"content":{}}""")) //cats
        server.enqueue(MockResponse().setBody("""{"content":{}}""")) //companies
        server.enqueue(MockResponse().setBody("""{"content":[]}""")) //flags
        server.enqueue(MockResponse().setBody("""{"content":[]}""")) //allergens
        server.enqueue(MockResponse().setBody("""{"content":[]}""")) //flavors
        server.enqueue(MockResponse().setBody("""{"content":[]}""")) //cuisines
        server.enqueue(MockResponse().setBody("""{"content":[]}""")) //times
    }

    @After
    fun tearDown() {
        stopKoin()
        server.shutdown()
    }

    fun app(contents: RuntimeScope.() -> Unit) {
        val runtime = createTestRuntime()
        runtime.assertUiState(SplashState::class.java, "app{} Init test")
        contents(RuntimeScope(runtime))
    }

    private fun <T: UiState> Runtime.setOrAssertState(name: String, setState: Boolean, testClass: Class<T>, uiState: UiState) {
        if (setState) {
            assertNoGlobalError("Setting default $name state")
            state = AppState.init().copy(uiState = uiState)
        } else {
            assertUiState(
                testClass,
                "Used $name block without being in $name state "
            )
        }
    }

    fun RuntimeScope.splash(setInitState: Boolean = false, contents: SplashScope.() -> Unit) {
        runtime.setOrAssertState("splash", setInitState, SplashState::class.java, SplashState)
        contents(SplashScope(this))
    }

    fun RuntimeScope.welcome(setInitState: Boolean = false, contents: WelcomeScope.() -> Unit) {
        runtime.setOrAssertState("welcome", setInitState, WelcomeState::class.java, WelcomeState)
        contents(WelcomeScope(this))
    }

    fun RuntimeScope.login(setInitState: Boolean = false, contents: LoginScope.() -> Unit) {
        runtime.setOrAssertState("login", setInitState, LoginState::class.java, LoginState.Entering.init())
        contents(LoginScope(this))
    }

    fun RuntimeScope.home(setInitState: Boolean = false, contents: HomeScope.() -> Unit) {
        runtime.setOrAssertState("home", setInitState, HomeState::class.java, HomeState)
        contents(HomeScope(this))
    }

    inner class RuntimeScope(val runtime: Runtime) {
        private val prefs: Prefs by KoinJavaComponent.inject(Prefs::class.java)

        fun assertNotLoggedIn() {
            assertEquals(prefs.user, null)
        }

        fun assertLoggedIn(user: User) {
            assertEquals(runtime.state.user?.name, user.name)
            assertEquals(runtime.state.user?.token, user.token)
            assertEquals(prefs.user, user)
        }
    }

    inner class SplashScope(private val scope: RuntimeScope) {
        fun init() {
            queuePreloadData()
            scope.runtime.receive(SplashAction.InitialiseApp)
        }
    }

    inner class WelcomeScope(private val scope: RuntimeScope) {
        fun goToLogin() {
            scope.runtime.receive(WelcomeAction.UserPressedLogin)
        }

        fun goToRegister() {
            scope.runtime.receive(WelcomeAction.UserPressedRegister)
        }
    }

    inner class LoginScope(private val scope: RuntimeScope) {
        fun enterValidDetailsAndSubmit(user: User, email: String = "", password: String = "") {
            server.enqueue(MockResponse().setBody("""{"content":{"name":"${user.name}","token":"${user.token}"}}"""))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(email))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(password))
            scope.runtime.receive(LoginAction.UserSubmitted)
        }

        fun enterInvalidDetailsAndSubmit(email: String = "", password: String = "") {
            server.enqueue(MockResponse().setBody("""{"error":{"error_codes":[103],"error_message":"invalid"}}"""))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(email))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(password))
            scope.runtime.receive(LoginAction.UserSubmitted)
        }

        fun assertErrorVisible(msg: String? = null) {
            val error = scope.runtime.assertUiState(LoginState.Error::class.java)
            msg?.let {
                assertEquals(error.message, it)
            }
        }

        fun clearError() {
            scope.runtime.receive(LoginAction.UserClearedError)
        }
    }

    inner class HomeScope(private val scope: RuntimeScope)

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            Timber.plant(ConsoleTree())
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            Timber.uprootAll()
        }
    }
}
