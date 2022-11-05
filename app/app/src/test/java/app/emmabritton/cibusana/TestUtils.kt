package app.emmabritton.cibusana

import app.emmabritton.cibusana.network.Logger
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.UiState
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

            override fun e(exception: Throwable, msg: String) {
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
    this.state.assertNoGlobalError(msg)
}

@Suppress("UNCHECKED_CAST")
fun <S : UiState> AppState.assertUiState(type: Class<S>, prefix: String? = null): S {
    assertNoGlobalError("Error found for $prefix")
    assert(this.uiState.javaClass == type) {
        "${prefix ?: ""}UI state should have been ${type.simpleName} was ${this.uiState.javaClass.simpleName}"
    }
    return this.uiState as S
}

fun <S : UiState> Runtime.assertUiState(type: Class<S>, prefix: String? = null): S {
    return this.state.assertUiState(type, prefix)
}