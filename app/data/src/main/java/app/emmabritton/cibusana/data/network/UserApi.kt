package app.emmabritton.cibusana.data.network

import app.emmabritton.cibusana.data.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<ResponseWrapper<LoginResponse>>

    @POST("/users/register")
    fun register(@Body request: RegisterRequest): Call<ResponseWrapper<RegisterResponse>>
}