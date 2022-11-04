package app.emmabritton.cibusana.persist

import org.koin.dsl.module

val persistModule = module {
    single {
        Prefs(get(), get())
    }

    single {
        UserController(get(), get())
    }
}