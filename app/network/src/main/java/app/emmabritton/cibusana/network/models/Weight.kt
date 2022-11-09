package app.emmabritton.cibusana.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class WeightResponse(
    @Json(name = "a")
    val amount: Float,
    @Json(name = "d")
    val date: ZonedDateTime
)

@JsonClass(generateAdapter = true)
data class WeightRequest(
    val amount: Float,
    val date: ZonedDateTime
)