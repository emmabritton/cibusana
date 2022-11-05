package app.emmabritton.cibusana.flow.foodList

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.system.Action

sealed class FoodAction : Action {
    object Show : FoodAction()
    class UserUpdateSearchTerm(val newText: String) : FoodAction()
    class SearchRejected(val errorCodes: List<Int>) : FoodAction()
    class ReplaceFood(val items: List<FoodResponse>) : FoodAction()
}