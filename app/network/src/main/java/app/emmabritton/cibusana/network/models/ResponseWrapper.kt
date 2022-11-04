package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWrapper<T>(
    val content: T?,
    val error: ResponseError?
)
