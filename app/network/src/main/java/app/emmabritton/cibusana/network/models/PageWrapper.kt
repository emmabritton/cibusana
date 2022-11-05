package app.emmabritton.cibusana.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageWrapper<T>(
    val items: List<T>,
    val page: Int
)