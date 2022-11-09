package app.emmabritton.cibusana.network.apis

import app.emmabritton.cibusana.network.HEADER_TOKEN
import app.emmabritton.cibusana.network.models.ResponseWrapper
import app.emmabritton.cibusana.network.models.WeightRequest
import app.emmabritton.cibusana.network.models.WeightResponse
import retrofit2.Call
import retrofit2.http.*
import java.time.ZonedDateTime
import java.util.UUID

interface MeApi {
    @GET("/me/weight")
    fun weight(
        @Header(HEADER_TOKEN) token: UUID,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<ResponseWrapper<List<WeightResponse>>>

    @GET("/me/weight/first")
    fun firstWeight(
        @Header(HEADER_TOKEN) token: UUID,
    ): Call<ResponseWrapper<WeightResponse?>>

    @GET("/me/weight/last")
    fun lastWeight(
        @Header(HEADER_TOKEN) token: UUID,
    ): Call<ResponseWrapper<WeightResponse?>>

    @POST("/me/weight")
    fun weight(
        @Header(HEADER_TOKEN) token: UUID,
        @Body body: WeightRequest
    ): Call<ResponseWrapper<Int>>
}