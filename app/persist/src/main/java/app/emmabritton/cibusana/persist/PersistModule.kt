package app.emmabritton.cibusana.persist

import org.koin.dsl.module

@Suppress("USELESS_CAST")
val persistModule = module {
    single {
        AndroidPrefs(get(), get()) as Prefs
    }

    single {
        UserController(get(), get())
    }

    single {
        DataController(get(), get())
    }

    single {
        FoodController(get())
    }
}