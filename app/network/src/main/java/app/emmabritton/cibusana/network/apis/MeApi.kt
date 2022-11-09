package app.emmabritton.cibusana.network.apis

import app.emmabritton.cibusana.network.HEADER_TOKEN
import app.emmabritton.cibusana.network.models.*
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

    @DELETE("/me/weight/{id}")
    fun weight(
        @Header(HEADER_TOKEN) token: UUID,
        @Path("id") id: ZonedDateTime
    ): Call<ResponseWrapper<Int>>

    @DELETE("/me/entry/{id}")
    fun entry(
        @Header(HEADER_TOKEN) token: UUID,
        @Path("id") id: Long
    ): Call<ResponseWrapper<Int>>

    @POST("/me/entry")
    fun entry(
        @Header(HEADER_TOKEN) token: UUID,
        @Body body: MealEntryRequest
    ): Call<ResponseWrapper<Long>>

    @GET("/me/entry/first")
    fun firstEntry(
        @Header(HEADER_TOKEN) token: UUID
    ): Call<ResponseWrapper<MealEntryResponse?>>

    @GET("/me/entry/last")
    fun lastEntry(
        @Header(HEADER_TOKEN) token: UUID
    ): Call<ResponseWrapper<MealEntryResponse?>>

    @GET("/me/entry")
    fun entry(
        @Header(HEADER_TOKEN) token: UUID,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<ResponseWrapper<List<MealEntryResponse>>>
}