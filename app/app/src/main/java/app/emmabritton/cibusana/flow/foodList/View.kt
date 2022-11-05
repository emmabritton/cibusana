package app.emmabritton.cibusana.flow.foodList

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.ui.theme.Dimen.Padding
import app.emmabritton.system.ActionReceiver

@ExperimentalMaterial3Api
@Composable
fun FoodUi(foodState: FoodState, actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        TextField(
            value = foodState.searchTerm,
            onValueChange = { actionReceiver.receive(FoodAction.UserUpdateSearchTerm(it)) },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth())

        if (foodState.loading) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(foodState.food) {
                    FoodElement(food = it)
                }
            }
        }
    }
}

@Composable
fun FoodElement(food: FoodResponse) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(Padding.Small)) {
        Text(food.name, style = MaterialTheme.typography.bodyLarge)
        if (food.company != null) {
            Text(food.company!!, style = MaterialTheme.typography.bodySmall)
        }
        Text("${food.caloriesPer100}kcal per 100g", style = MaterialTheme.typography.bodySmall)
    }
}