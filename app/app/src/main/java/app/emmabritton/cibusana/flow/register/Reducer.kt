package app.emmabritton.cibusana.flow.register

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidState

fun reduceRegisterAction(action: RegisterAction, state: AppState): AppEffect {
    if (state.uiState !is RegisterState) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}, (must be RegisterState)")
    }
    if (!action.stateValidator(state.uiState)) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}")
    }
    return when (action) {
        is RegisterAction.UserUpdatedEmail -> AppEffect(
            state.copy(
                uiState = (state.uiState as RegisterState.Entering).copy(email = action.newText)
            ), emptyList()
        )
        is RegisterAction.UserUpdatedPassword -> AppEffect(
            state.copy(
                uiState = (state.uiState as RegisterState.Entering).copy(password = action.newText)
            ), emptyList()
        )
        is RegisterAction.UserUpdatedName -> AppEffect(
            state.copy(
                uiState = (state.uiState as RegisterState.Entering).copy(name = action.newText)
            ), emptyList()
        )
        is RegisterAction.UserSubmitted -> {
            AppEffect(
                state.copy(uiState = (state.uiState as RegisterState.Entering).toLoading()),
                listOf(SubmitUserRegister(state.uiState.email, state.uiState.password, state.uiState.name))
            )
        }
        is RegisterAction.Rejected -> {
            val text = if (action.reasons.isEmpty()) {
                "Network error"
            } else {
                action.reasons.joinToString(", ")
            }

            AppEffect(
                state.copy(
                    uiState = (state.uiState as RegisterState.Loading).toError(text)
                ), emptyList()
            )
        }
        is RegisterAction.UserClearedError -> AppEffect(
            state.copy(uiState = (state.uiState as RegisterState.Error).toEntering()),
            emptyList()
        )
    }
}