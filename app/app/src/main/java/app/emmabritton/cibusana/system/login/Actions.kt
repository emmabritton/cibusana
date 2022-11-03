package app.emmabritton.cibusana.system.login

import app.emmabritton.cibusana.system.AppEffect
import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.InvalidAction
import app.emmabritton.system.Action
import app.emmabritton.system.Effect

sealed class LoginAction(val stateValidator: (LoginState) -> Boolean) : Action

private val enteringOnly = { state: LoginState -> state is LoginState.Entering }
private val loadingOnly = { state: LoginState -> state is LoginState.Loading }
private val errorOnly = { state: LoginState -> state is LoginState.Error }

data class UserUpdatedLoginEmail(val newText: String) : LoginAction(enteringOnly)
data class UserUpdatedLoginPassword(val newText: String) : LoginAction(enteringOnly)
object UserSubmittedLogin : LoginAction(enteringOnly)
object UserClearedError : LoginAction(errorOnly)
class LoginRejected(val reasons: List<Int>) : LoginAction(loadingOnly)
class LoginAccepted(val name: String, val token: String) : LoginAction(loadingOnly)

fun reduceLoginAction(action: LoginAction, state: AppState): AppEffect {
    if (!action.stateValidator(state.loginState)) {
        throw java.lang.IllegalStateException("$action doesn't support ${state.loginState}")
    }
    return when (action) {
        is UserUpdatedLoginEmail -> AppEffect(
            state.copy(
                loginState = LoginState.Entering(
                    email = action.newText,
                    password = (state.loginState as LoginState.Entering).password
                )
            ), emptyList()
        )
        is UserUpdatedLoginPassword -> AppEffect(
            state.copy(
                loginState = LoginState.Entering(
                    email = (state.loginState as LoginState.Entering).email,
                    password = action.newText
                )
            ), emptyList()
        )
        is UserSubmittedLogin -> {
            AppEffect(
                state.copy(loginState = (state.loginState as LoginState.Entering).toLoading()),
                listOf(SubmitUserLogin(state.loginState.email, state.loginState.password))
            )
        }
        is LoginRejected -> {
            val text = if (action.reasons.isEmpty()) {
                "Network error"
            } else {
                action.reasons.joinToString(", ")
            }

            AppEffect(
                state.copy(
                    loginState = (state.loginState as LoginState.Loading).toError(text)
                ), emptyList()
            )
        }
        is UserClearedError -> AppEffect(
            state.copy(loginState = (state.loginState as LoginState.Error).toEntering()),
            emptyList()
        )
        is LoginAccepted -> AppEffect(
            state.copy(loginState = LoginState.LoggedIn(action.name, action.token)),
            emptyList()
        )
    }
}