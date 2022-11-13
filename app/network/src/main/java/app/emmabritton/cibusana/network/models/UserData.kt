package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class UserDataRequest(
    val height: Int,
    val units: String,
    val measurement_names: List<String>,
    val target_weight_grams: Int?,
    val target_weight_date: ZonedDateTime?
)

@JsonClass(generateAdapter = true)
data class UserDataResponse(
    val height: Int,
    val units: String,
    val measurement_names: List<String>,
    val target_weight_grams: Int?,
    val target_weight_date: ZonedDateTime?
)