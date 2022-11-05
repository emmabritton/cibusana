package app.emmabritton.cibusana.network.network

import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {
    @GET("/food")
    fun searchFoods(@Query("page") page: Int, @Query("name") partialName: String?)
}