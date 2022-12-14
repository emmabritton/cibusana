package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime
import java.util.*

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val height: Int,
    val units: String,
    val target_weight_grams: Int?,
    val target_weight_date: ZonedDateTime?
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(val token: UUID)
