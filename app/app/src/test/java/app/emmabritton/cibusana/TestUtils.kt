package app.emmabritton.cibusana

import app.emmabritton.cibusana.data.Logger
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.login.LoginState
import app.emmabritton.system.ImmediateCommandHandler
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import kotlin.test.assertNull

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

            override fun e(exception: Exception, msg: String) {
                println("E: $msg: ${exception.javaClass.simpleName}")
                exception.printStackTrace()
            }
        } as Logger
    }
}

fun AppState.assertNoGlobalError(msg: String? = null) {
    assertNull(this.error, msg ?: "Error should have be null")
}

fun Runtime.assertNoGlobalError(msg: String? = null) {
    this.state().assertNoGlobalError(msg)
}

@Suppress("UNCHECKED_CAST")
fun <S : LoginState> AppState.assertLoginState(type: Class<S>, prefix: String? = null): S {
    assert(this.loginState.javaClass == type) {
        "${prefix ?: ""}Login state should have been ${type.simpleName} was ${this.loginState.javaClass.simpleName}"
    }
    return this.loginState as S
}

fun <S : LoginState> Runtime.assertLoginState(type: Class<S>, prefix: String? = null): S {
    return this.state().assertLoginState(type, prefix)
}