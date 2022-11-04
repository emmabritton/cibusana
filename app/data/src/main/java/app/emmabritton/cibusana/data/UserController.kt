package app.emmabritton.cibusana.data

import app.emmabritton.cibusana.data.models.*
import app.emmabritton.cibusana.data.network.*

class UserController(private val userApi: UserApi, private val logger: Logger) {
    fun login(email: String, password: String): Result<LoginResponse> {
        val request = LoginRequest(email, password)

        return executeRequest(logger, userApi.login(request))
    }

    fun register(email: String, password: String, name: String): Result<RegisterResponse> {
        val request = RegisterRequest(email, password, name)

        return executeRequest(logger, userApi.register(request))
    }
}