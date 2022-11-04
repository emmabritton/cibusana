package app.emmabritton.cibusana.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseError(
    @Json(name = "error_codes")
    val codes: List<Int>,
    @Json(name = "error_message")
    val message: String
)
