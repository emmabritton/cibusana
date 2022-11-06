package app.emmabritton.cibusana.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.UUID

class UuidAdapter : JsonAdapter<UUID>() {
    override fun fromJson(reader: JsonReader): UUID? {
        return UUID.fromString(reader.nextString())
    }

    override fun toJson(writer: JsonWriter, value: UUID?) {
        value?.let {
            writer.jsonValue(it.toString())
        }
    }
}