package app.emmabritton.cibusana.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class WeightResponse(
    @Json(name = "g")
    val grams: Int,
    @Json(name = "d")
    val date: ZonedDateTime
)

@JsonClass(generateAdapter = true)
data class WeightRequest(
    val grams: Int,
    val date: ZonedDateTime
)