package app.emmabritton.cibusana.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class FoodResponse(
    val id: UUID,
    val name: String,
    @Json(name = "calories_p100")
    val caloriesPer100: Int,
    @Json(name = "carbs_p100")
    val carbsPer100: Float,
    @Json(name = "fat_p100")
    val fatPer100: Float,
    @Json(name = "protein_p100")
    val proteinPer100: Float,
    @Json(name = "serving_sizes")
    val servingSizes: Map<String, Int>,
    val company: String?,
    val range: String?,
    val category: String,
    @Json(name = "sub_category")
    val subCategory: String,
    val allergens: List<String>,
    val flags: List<String>
)