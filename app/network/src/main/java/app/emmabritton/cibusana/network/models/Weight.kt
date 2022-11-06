package app.emmabritton.cibusana.network.models

import java.time.ZonedDateTime

data class WeightRequest(
    val kg: Float,
    val date: ZonedDateTime
)