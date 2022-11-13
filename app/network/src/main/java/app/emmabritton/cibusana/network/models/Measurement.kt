package app.emmabritton.cibusana.network.models

import java.time.ZonedDateTime

data class MeasurementRequest(
    val date: ZonedDateTime,
    val measurements: Map<String, Int>
)

data class MeasurementResponse(
    val date: ZonedDateTime,
    val measurements: Map<String, Int>
)