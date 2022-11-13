package app.emmabritton.cibusana.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UuidAdapter : JsonAdapter<UUID>() {
    override fun fromJson(reader: JsonReader): UUID? {
        return UUID.fromString(reader.nextString())
    }

    override fun toJson(writer: JsonWriter, value: UUID?) {
        if (value != null) {
            writer.jsonValue(value.toString())
        } else {
            writer.nullValue()
        }
    }
}

class ZonedDateTimeAdapter : JsonAdapter<ZonedDateTime>() {
    override fun fromJson(reader: JsonReader): ZonedDateTime? {
        return ZonedDateTime.parse(reader.nextString())
    }

    override fun toJson(writer: JsonWriter, value: ZonedDateTime?) {
        if (value != null) {
            writer.jsonValue(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        } else {
            writer.nullValue()
        }
    }
}