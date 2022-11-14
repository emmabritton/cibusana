package app.emmabritton.cibusana.di

import android.content.Context
import app.emmabritton.cibusana.DateTimePrinter
import app.emmabritton.cibusana.network.DI_CACHE_FILE
import app.emmabritton.cibusana.network.DI_URL
import app.emmabritton.cibusana.network.Logger
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

//Intellij says the cast is not needed, but is for Koin
@Suppress("USELESS_CAST")
val appModule = module {
    single {
        HttpLoggingInterceptor {
            Timber.tag("HTTP").d(it)
        } as Interceptor
    }

    single {
        object : Logger {
            override fun d(msg: String) {
                Timber.d(msg)
            }

            override fun e(msg: String) {
                Timber.e(msg)
            }

            override fun e(exception: Throwable, msg: String) {
                Timber.e(exception, msg)
            }
        } as Logger
    }

    single(named(DI_URL)) {
        "https://cibusana.com"
    }

    single(named(DI_CACHE_FILE)) {
        get<Context>().cacheDir
    }

    single {
        DateTimePrinter(get())
    }
}