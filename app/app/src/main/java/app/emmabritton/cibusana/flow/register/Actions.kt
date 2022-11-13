package app.emmabritton.cibusana.flow.register

import app.emmabritton.system.Action

private val enteringOnly = { state: RegisterState -> state is RegisterState.Entering }
private val loadingOnly = { state: RegisterState -> state is RegisterState.Loading }
private val errorOnly = { state: RegisterState -> state is RegisterState.Error }

sealed class RegisterAction(val stateValidator: (RegisterState) -> Boolean) : Action {
    /**
     * User has entered text into the email field on the register screen
     */
    data class UserUpdatedEmail(val newText: String) : RegisterAction(enteringOnly)
    /**
     * User has entered text into the password field on the register screen
     */
    data class UserUpdatedPassword(val newText: String) : RegisterAction(enteringOnly)
    /**
     * User has entered text into the name field on the register screen
     */
    data class UserUpdatedName(val newText: String) : RegisterAction(enteringOnly)
    /**
     * User has entered a number into the height field on the register screen
     */
    data class UserUpdatedHeight(val newHeight: Int): RegisterAction(enteringOnly)
    /**
     * User has pressed submit button on the register screen
     */
    object UserSubmitted : RegisterAction(enteringOnly)
    /**
     * User has closed the error register screen
     */
    object UserClearedError : RegisterAction(errorOnly)
    /**
     * Server rejected register request
     */
    class Rejected(val reasons: List<Int>) : RegisterAction(loadingOnly)
}