package app.emmabritton.cibusana.network.apis

import app.emmabritton.cibusana.network.models.FoodResponse
import app.emmabritton.cibusana.network.models.PageWrapper
import app.emmabritton.cibusana.network.models.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

internal interface FoodApi {
    @GET("/food")
    fun searchFoods(
        @Query("page") page: Int,
        @Query("name") partialName: String?
    ): Call<ResponseWrapper<PageWrapper<FoodResponse>>>

    @GET("/food/:id")
    fun food(
        @Path("id") id: UUID
    ): Call<ResponseWrapper<FoodResponse?>>
}