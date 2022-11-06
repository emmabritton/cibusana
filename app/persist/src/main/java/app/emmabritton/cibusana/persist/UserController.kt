package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.persist.models.User
import java.time.ZonedDateTime
import java.util.UUID

class UserController(private val api: Api, private val prefs: Prefs) {
    fun login(email: String, password: String) = api.login(email, password)

    fun register(email: String, password: String, name: String) =
        api.register(email, password, name)

    fun getWeights(start: ZonedDateTime, end: ZonedDateTime): Result<Map<ZonedDateTime, Float>> = loggedInRequest { api.getWeights(it, start, end) }
    fun setWeight(kgs: Float, date: ZonedDateTime): Result<Unit> = loggedInRequest { api.setWeight(it, kgs, date).map { } }

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