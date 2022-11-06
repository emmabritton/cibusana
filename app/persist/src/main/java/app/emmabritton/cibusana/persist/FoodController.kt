package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.network.Logger
import app.emmabritton.cibusana.network.models.FoodResponse
import java.util.UUID

class FoodController(private val api: Api) {
    fun search(text: String, page: Int): Result<Pair<Int, List<FoodResponse>>> {
        var searchText: String? = text
        if (text.isBlank()) {
            searchText = null
        }
        return api.searchFood(searchText, page)
    }

    fun exact(id: UUID) = api.getFood(id)
}