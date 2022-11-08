package app.emmabritton.cibusana

import android.util.Log
import org.intellij.lang.annotations.Language
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class ConsoleTree : Timber.DebugTree() {

    @Language("RegExp")
    private val anonymousClassPattern = Pattern.compile("""(\$\d+)+$""")

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val dateTime = LocalDateTime.now().format(dateTimeFormatter)
        val priorityChar = when (priority) {
            Log.VERBOSE -> 'V'
            Log.DEBUG -> 'D'
            Log.INFO -> 'I'
            Log.WARN -> 'W'
            Log.ERROR -> 'E'
            Log.ASSERT -> 'A'
            else -> '?'
        }

        println("$dateTime $priorityChar/$tag: $message")
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        val matcher = anonymousClassPattern.matcher(element.className)
        val tag = when {
            matcher.find() -> matcher.replaceAll("")
            else -> element.className
        }
        return tag.substringAfterLast('.')
    }
}