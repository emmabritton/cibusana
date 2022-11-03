package app.emmabritton.cibusana.data.network

import app.emmabritton.cibusana.data.models.LoginRequest
import app.emmabritton.cibusana.data.models.LoginResponse
import app.emmabritton.cibusana.data.models.ResponseWrapper
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<ResponseWrapper<LoginResponse>>
}