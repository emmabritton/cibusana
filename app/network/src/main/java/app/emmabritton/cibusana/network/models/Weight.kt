package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class WeightRequest(
    val amount: Float,
    val date: ZonedDateTime
)