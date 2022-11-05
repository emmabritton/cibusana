package app.emmabritton.cibusana.system

import app.emmabritton.system.Action

class InvalidState(msg: String) :
    AppEffect(AppState.init().copy(error = "Invalid state transition: $msg"), emptyList())

class UnknownAction(msg: String) :
    AppEffect(AppState.init().copy(error = "Unknown action: $msg"), emptyList())

class UnknownUiState(private val name: String) : Action {
    fun toEffect() = AppEffect(AppState.init().copy(error = "Unknown UI state: $name"), emptyList())
}

class GoBack(val state: UiState) : Action {
    override fun describe() = "GoToState(${state.javaClass.simpleName})"
}