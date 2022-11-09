package app.emmabritton.cibusana.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime
import java.util.UUID

@JsonClass(generateAdapter = true)
data class MealEntryRequest(
    @Json(name = "food_id")
    val foodId: UUID,
    val amount: Int,
    val calories: Int,
    @Json(name = "is_meal")
    val isMeal: Boolean,
    val date: ZonedDateTime,
    @Json(name = "meal_time")
    val mealTime: String
)

@JsonClass(generateAdapter = true)
data class MealEntryResponse(
    val id: Long,
    @Json(name = "food_id")
    val foodId: UUID,
    val grams: Int,
    val calories: Int,
    @Json(name = "is_meal")
    val isMeal: Boolean,
    val date: ZonedDateTime,
    @Json(name = "meal_time")
    val mealTime: String
)