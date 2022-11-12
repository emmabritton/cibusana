package app.emmabritton.cibusana.internal.scopes

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.flow.login.LoginAction
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.internal.assertUiState
import app.emmabritton.cibusana.persist.models.User
import okhttp3.mockwebserver.MockResponse
import kotlin.test.assertEquals

class LoginScope(private val scope: RuntimeTest.RuntimeScope) {
        fun enterValidDetailsAndSubmit(user: User, email: String = "", password: String = "") {
            scope.server.enqueue(MockResponse().setBody("""{"content":{"name":"${user.name}","token":"${user.token}"}}"""))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(email))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(password))
            scope.runtime.receive(LoginAction.UserSubmitted)
        }

        fun enterInvalidDetailsAndSubmit(email: String = "", password: String = "") {
            scope.server.enqueue(MockResponse().setBody("""{"error":{"error_codes":[103],"error_message":"invalid"}}"""))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(email))
            scope.runtime.receive(LoginAction.UserUpdatedEmail(password))
            scope.runtime.receive(LoginAction.UserSubmitted)
        }

        fun assertErrorVisible(msg: String? = null) {
            val error = scope.runtime.assertUiState(LoginState.Error::class.java)
            msg?.let {
                assertEquals(error.message, it)
            }
        }

        fun clearError() {
            scope.runtime.receive(LoginAction.UserClearedError)
        }
    }