package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.network.models.WeightResponse
import app.emmabritton.cibusana.persist.models.User
import java.time.ZonedDateTime
import java.util.UUID

class UserController(private val api: Api, private val prefs: Prefs) {
    fun login(email: String, password: String) = api.login(email, password)

    fun register(email: String, password: String, name: String) =
        api.register(email, password, name)

    fun getWeights(start: ZonedDateTime, end: ZonedDateTime) = loggedInRequest { api.getWeights(it, start, end) }
    fun setWeight(kgs: Float, date: ZonedDateTime) = loggedInRequest { api.setWeight(it, kgs, date).map { } }
    fun getLastWeight() = loggedInRequest { api.getLastWeight(it) }
    fun getFirstWeight() = loggedInRequest { api.getFirstWeight(it) }

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