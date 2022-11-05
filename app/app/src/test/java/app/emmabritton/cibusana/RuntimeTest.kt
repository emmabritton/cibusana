package app.emmabritton.cibusana

import app.emmabritton.cibusana.network.DI_URL
import app.emmabritton.cibusana.network.networkModule
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.Prefs
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.cibusana.persist.models.User
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

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

    @After
    fun tearDown() {
        stopKoin()
        server.shutdown()
    }
}