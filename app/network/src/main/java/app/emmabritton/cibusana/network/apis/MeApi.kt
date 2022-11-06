package app.emmabritton.cibusana.network.apis

import app.emmabritton.cibusana.network.HEADER_TOKEN
import app.emmabritton.cibusana.network.models.ResponseWrapper
import app.emmabritton.cibusana.network.models.WeightRequest
import retrofit2.Call
import retrofit2.http.*
import java.time.ZonedDateTime
import java.util.UUID

interface MeApi {
    @GET("/me/weight")
    fun weight(
        @Header(HEADER_TOKEN) token: UUID,
        @Query("start") start: ZonedDateTime,
        @Query("end") end: ZonedDateTime
    ): Call<ResponseWrapper<Map<ZonedDateTime, Float>>>

    @POST("/me/weight")
    fun weight(
        @Header(HEADER_TOKEN) token: UUID,
        @Body body: WeightRequest
    ): Call<ResponseWrapper<Int>>
}