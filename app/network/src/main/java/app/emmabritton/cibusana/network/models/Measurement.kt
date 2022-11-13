package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class MeasurementRequest(
    val date: ZonedDateTime,
    val measurements: Map<String, Float>
)

@JsonClass(generateAdapter = true)
data class MeasurementResponse(
    val date: ZonedDateTime,
    val measurements: Map<String, Float>
)