package app.emmabritton.cibusana

import android.content.Context
import android.text.format.DateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimePrinter(private val context: Context) {
    fun formatTime(zonedDateTime: ZonedDateTime): String {
        val is24Hour = DateFormat.is24HourFormat(context)
        val isAmerican = Locale.getDefault() == Locale.US
        //handle 24 hour and american variants
        return DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime)
    }

    fun formatShortDate(zonedDateTime: ZonedDateTime): String {
        val isAmerican = Locale.getDefault() == Locale.US
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(zonedDateTime)
    }

    fun formatDate(zonedDateTime: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("dd MMM yyyy").format(zonedDateTime)
    }
}