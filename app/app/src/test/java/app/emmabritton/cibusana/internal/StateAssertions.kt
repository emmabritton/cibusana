package app.emmabritton.cibusana.internal

import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.Runtime
import app.emmabritton.cibusana.system.UiState
import kotlin.test.assertNull

fun AppState.assertNoGlobalError(msg: String? = null) {
    assertNull(this.error, msg ?: "Error should have be null")
}

fun Runtime.assertNoGlobalError(msg: String? = null) {
    this.state.assertNoGlobalError(msg)
}

@Suppress("UNCHECKED_CAST")
fun <S : UiState> AppState.assertUiState(type: Class<S>, prefix: String? = null): S {
    assertNoGlobalError("Error found for $prefix")
    assert(this.uiState.javaClass == type || this.uiState.javaClass.enclosingClass == type) {
        "${prefix ?: ""} UI state should have been $type was ${this.uiState.javaClass}"
    }
    return this.uiState as S
}

fun <S : UiState> Runtime.assertUiState(type: Class<S>, prefix: String? = null): S {
    return this.state.assertUiState(type, prefix)
}