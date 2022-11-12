package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.network.models.MealEntryRequest
import app.emmabritton.cibusana.persist.models.User
import java.time.ZonedDateTime
import java.util.*

class UserController(private val api: Api, private val prefs: Prefs) {
    fun login(email: String, password: String) = api.login(email, password)

    fun register(email: String, password: String, name: String) =
        api.register(email, password, name)

    fun getWeights(start: ZonedDateTime, end: ZonedDateTime) = loggedInRequest { api.getWeights(it, start, end) }
    fun addWeight(kgs: Float, date: ZonedDateTime) = loggedInRequest { api.addWeight(it, kgs, date).map { } }
    fun getFirstWeight() = loggedInRequest { api.getFirstWeight(it) }
    fun getLastWeight() = loggedInRequest { api.getLastWeight(it) }
    fun deleteWeight(date: ZonedDateTime) = loggedInRequest { api.deleteWeight(it, date) }

    fun getFirstEntry() = loggedInRequest { api.getFirstEntry(it) }
    fun getLastEntry() = loggedInRequest { api.getLastEntry(it) }
    fun getEntries(start: ZonedDateTime, end: ZonedDateTime) = loggedInRequest { api.getEntries(it, start, end) }
    fun addEntry(entry: MealEntryRequest) = loggedInRequest { api.addEntry(it, entry) }
    fun deleteEntry(id: Long) = loggedInRequest { api.deleteEntry(it, id) }

    private fun <T> loggedInRequest(method: (UUID) -> Result<T>): Result<T> {
        val user = user
        return if (user == null) {
            Result.failure(NotLoggedInException)
        } else {
            method(user.token)
        }
    }

    var user: User?
        set(value) {
            prefs.user = value
        }
        get() = prefs.user
}