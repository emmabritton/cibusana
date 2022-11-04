package app.emmabritton.cibusana

import android.app.Application
import android.content.Context
import app.emmabritton.cibusana.di.appModule
import app.emmabritton.cibusana.network.networkModule
import app.emmabritton.cibusana.persist.persistModule
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class CibusanaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            logger(AndroidLogger())
            modules(module { single{
                @Suppress("USELESS_CAST")
                this@CibusanaApp as Context
            } }, appModule, networkModule, persistModule)
        }
    }
}