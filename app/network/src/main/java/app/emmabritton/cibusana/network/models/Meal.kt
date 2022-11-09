package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class MealResponse(
    val id: UUID,
    val name: String
)