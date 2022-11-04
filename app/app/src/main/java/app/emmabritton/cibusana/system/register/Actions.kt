package app.emmabritton.cibusana.system.register

import app.emmabritton.system.Action

private val enteringOnly = { state: RegisterState -> state is RegisterState.Entering }
private val loadingOnly = { state: RegisterState -> state is RegisterState.Loading }
private val errorOnly = { state: RegisterState -> state is RegisterState.Error }

sealed class RegisterAction(val stateValidator: (RegisterState) -> Boolean) : Action {
    data class UserUpdatedEmail(val newText: String) : RegisterAction(enteringOnly)
    data class UserUpdatedPassword(val newText: String) : RegisterAction(enteringOnly)
    data class UserUpdatedName(val newText: String) : RegisterAction(enteringOnly)
    object UserSubmitted : RegisterAction(enteringOnly)
    object UserClearedError : RegisterAction(errorOnly)
    class Rejected(val reasons: List<Int>) : RegisterAction(loadingOnly)
    class Accepted(val name: String, val token: String) : RegisterAction(loadingOnly)
}