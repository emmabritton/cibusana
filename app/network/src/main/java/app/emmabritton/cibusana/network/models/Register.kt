package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(val email: String, val password: String, val name: String)

@JsonClass(generateAdapter = true)
data class RegisterResponse(val token: String)
