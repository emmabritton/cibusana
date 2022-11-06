package app.emmabritton.cibusana.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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

class ZonedDateTimeAdapter : JsonAdapter<ZonedDateTime>() {
    override fun fromJson(reader: JsonReader): ZonedDateTime? {
        return ZonedDateTime.parse(reader.nextString())
    }

    override fun toJson(writer: JsonWriter, value: ZonedDateTime?) {
        value?.let {
            writer.jsonValue(it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        }
    }
}