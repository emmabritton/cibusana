package app.emmabritton.cibusana.network

import app.emmabritton.cibusana.network.models.LoginRequest
import app.emmabritton.cibusana.network.models.LoginResponse
import app.emmabritton.cibusana.network.models.RegisterRequest
import app.emmabritton.cibusana.network.models.RegisterResponse
import app.emmabritton.cibusana.network.network.UserApi
import app.emmabritton.cibusana.network.network.executeRequest

class Api(private val userApi: UserApi, private val logger: Logger) {
    fun login(email: String, password: String): Result<LoginResponse> {
        val request = LoginRequest(email, password)

        return executeRequest(logger, userApi.login(request))
    }

    fun register(email: String, password: String, name: String): Result<RegisterResponse> {
        val request = RegisterRequest(email, password, name)

        return executeRequest(logger, userApi.register(request))
    }
}