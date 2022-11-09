package app.emmabritton.cibusana

import app.emmabritton.cibusana.network.exceptions.BadRequestException
import java.time.ZonedDateTime

fun Throwable.errorCodes(): List<Int> {
    return if (this is BadRequestException) {
        this.codes
    } else {
        emptyList()
    }
}

fun ZonedDateTime.withStartOfDay() = this.withHour(0).withMinute(0).withSecond(0)
fun ZonedDateTime.withEndOfDay() = this.withHour(23).withMinute(59).withSecond(59)