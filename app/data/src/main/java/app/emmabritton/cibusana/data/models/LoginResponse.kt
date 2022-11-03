package app.emmabritton.cibusana.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(val name: String, val token: String)
