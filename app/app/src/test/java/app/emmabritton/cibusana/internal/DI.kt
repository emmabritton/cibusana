package app.emmabritton.cibusana.internal

import app.emmabritton.cibusana.network.Logger
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.system.ImmediateCommandHandler
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

fun createTestRuntime(): Runtime {
    return Runtime({ method -> method() }, ImmediateCommandHandler()) {}
}

@Suppress("USELESS_CAST")
val testModule = module {
    single {
        HttpLoggingInterceptor() as Interceptor
    }
    single {
        object : Logger {
            override fun d(msg: String) {
                println("D: $msg")
            }

            override fun e(msg: String) {
                println("E: $msg")
            }

            override fun e(exception: Throwable, msg: String) {
                println("E: $msg: ${exception.javaClass.simpleName}")
                exception.printStackTrace()
            }
        } as Logger
    }
}

