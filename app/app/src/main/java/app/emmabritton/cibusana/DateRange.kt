package app.emmabritton.cibusana

import java.time.ZonedDateTime

data class DateRange(val start: ZonedDateTime, val end: ZonedDateTime) {
    companion object {
        fun init() = DateRange(ZonedDateTime.now().minusDays(30), ZonedDateTime.now())
    }
}