package app.emmabritton.cibusana.flow.login

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidState

fun reduceLoginAction(action: LoginAction, state: AppState): AppEffect {
    if (state.uiState !is LoginState) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}, (must be LoginState)")
    }
    if (!action.stateValidator(state.uiState)) {
        return InvalidState("Tried to run ${action.describe()} for ${state.uiState.javaClass.simpleName}")
    }
    return when (action) {
        is LoginAction.UserUpdatedEmail -> AppEffect(
            state.copy(
                uiState = (state.uiState as LoginState.Entering).copy(email = action.newText)
            ), emptyList()
        )
        is LoginAction.UserUpdatedPassword -> AppEffect(
            state.copy(
                uiState = (state.uiState as LoginState.Entering).copy(password = action.newText)
            ), emptyList()
        )
        is LoginAction.UserSubmitted -> {
            AppEffect(
                state.copy(uiState = (state.uiState as LoginState.Entering).toLoading()),
                listOf(SubmitUserLogin(state.uiState.email, state.uiState.password))
            )
        }
        is LoginAction.Rejected -> {
            val text = if (action.reasons.isEmpty()) {
                "Network error"
            } else {
                action.reasons.joinToString(", ")
            }

            AppEffect(
                state.copy(
                    uiState = (state.uiState as LoginState.Loading).toError(text)
                ), emptyList()
            )
        }
        is LoginAction.UserClearedError -> AppEffect(
            state.copy(uiState = (state.uiState as LoginState.Error).toEntering()),
            emptyList()
        )
    }
}