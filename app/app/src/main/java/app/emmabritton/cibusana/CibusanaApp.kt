package app.emmabritton.cibusana

import android.app.Application
import app.emmabritton.cibusana.data.dataModule
import app.emmabritton.cibusana.di.appModule
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CibusanaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            logger(AndroidLogger())
            modules(appModule,dataModule)
        }
    }
}