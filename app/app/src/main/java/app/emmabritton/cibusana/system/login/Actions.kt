package app.emmabritton.cibusana.system.login

import app.emmabritton.system.Action

private val enteringOnly = { state: LoginState -> state is LoginState.Entering }
private val loadingOnly = { state: LoginState -> state is LoginState.Loading }
private val errorOnly = { state: LoginState -> state is LoginState.Error }

sealed class LoginAction(val stateValidator: (LoginState) -> Boolean) : Action {
    data class UserUpdatedEmail(val newText: String) : LoginAction(enteringOnly)
    data class UserUpdatedPassword(val newText: String) : LoginAction(enteringOnly)
    object UserSubmitted : LoginAction(enteringOnly)
    object UserClearedError : LoginAction(errorOnly)
    class Rejected(val reasons: List<Int>) : LoginAction(loadingOnly)
    class Accepted(val name: String, val token: String) : LoginAction(loadingOnly)
}