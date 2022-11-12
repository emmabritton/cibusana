package app.emmabritton.cibusana

import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.internal.*
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
import kotlin.collections.ArrayDeque
import kotlin.test.assertEquals

open class RuntimeTest {
    private lateinit var server: MockWebServer

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

    @After
    fun tearDown() {
        stopKoin()
        server.shutdown()
    }

    fun app(contents: RuntimeScope.() -> Unit) {
        val runtime = createTestRuntime()
        runtime.assertUiState(SplashState::class.java, "app{} Init test")
        contents(RuntimeScope(runtime, server))
    }

    fun <T, C: UiState> RuntimeScope.skipTo(factory: ScopeFactory<T, C>, fakeHistory: List<UiState>, contents: T.() -> Unit) {
        runtime.assertNoGlobalError("Setting default $factory.name state")
        runtime.state = AppState.init().copy(uiState = factory.uiState, uiHistory = ArrayDeque(fakeHistory))
        contents(factory.makeScope(this))
    }

    fun <T, C: UiState> RuntimeScope.verifyAt(factory: ScopeFactory<T, C>, contents: T.() -> Unit) {
        runtime.assertUiState(
            factory.testClass,
            "Used ${factory.name} block without being in ${factory.name} state "
        )
        contents(factory.makeScope(this))
    }

    class RuntimeScope(val runtime: Runtime, val server: MockWebServer) {
        private val prefs: Prefs by KoinJavaComponent.inject(Prefs::class.java)

        fun assertNotLoggedIn() {
            assertEquals(prefs.user, null)
        }

        fun assertLoggedIn(user: User) {
            assertEquals(runtime.state.user?.name, user.name)
            assertEquals(runtime.state.user?.token, user.token)
            assertEquals(prefs.user, user)
        }

        fun enqueuePreloadData() {
            //has to match order in command PreLoadCacheData
            server.enqueue(MockResponse().setBody("""{"content":{}}""")) //cats
            server.enqueue(MockResponse().setBody("""{"content":{}}""")) //companies
            server.enqueue(MockResponse().setBody("""{"content":[]}""")) //flags
            server.enqueue(MockResponse().setBody("""{"content":[]}""")) //allergens
            server.enqueue(MockResponse().setBody("""{"content":[]}""")) //flavors
            server.enqueue(MockResponse().setBody("""{"content":[]}""")) //cuisines
            server.enqueue(MockResponse().setBody("""{"content":[]}""")) //times
        }
    }

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
