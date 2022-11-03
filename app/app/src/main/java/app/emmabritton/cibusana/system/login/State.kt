package app.emmabritton.cibusana.system.login

sealed class LoginState {
    data class Entering(val email: String, val password: String): LoginState() {
        fun toLoading() = Loading(this)
    }
    data class Loading(val details: Entering): LoginState() {
        fun toError(msg: String) = Error(msg, details)
    }
    data class Error(val message: String, val details: Entering): LoginState() {
        fun toEntering() = Entering(details.email, details.password)
    }

    data class LoggedIn(val name: String, val token: String): LoginState()
}