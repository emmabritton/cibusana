package app.emmabritton.cibusana.flow.login

import app.emmabritton.system.Action

private val enteringOnly = { state: LoginState -> state is LoginState.Entering }
private val loadingOnly = { state: LoginState -> state is LoginState.Loading }
private val errorOnly = { state: LoginState -> state is LoginState.Error }

sealed class LoginAction(val stateValidator: (LoginState) -> Boolean) : Action {
    /**
     * User has entered text into the email field on the login screen
     */
    data class UserUpdatedEmail(val newText: String) : LoginAction(enteringOnly)
    /**
     * User has entered text into the password field on the login screen
     */
    data class UserUpdatedPassword(val newText: String) : LoginAction(enteringOnly)
    /**
     * User has pressed submit button on the login screen
     */
    object UserSubmitted : LoginAction(enteringOnly)
    /**
     * User has closed the error login screen
     */
    object UserClearedError : LoginAction(errorOnly)
    /**
     * Server rejected login request
     */
    class Rejected(val reasons: List<Int>) : LoginAction(loadingOnly)
}