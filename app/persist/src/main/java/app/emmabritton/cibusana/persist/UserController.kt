package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.network.Logger
import app.emmabritton.cibusana.network.models.*
import app.emmabritton.cibusana.network.network.*
import app.emmabritton.cibusana.persist.models.User

class UserController(private val api: Api, private val prefs: Prefs) {
    fun login(email: String, password: String) = api.login(email, password)

    fun register(email: String, password: String, name: String) =
        api.register(email, password, name)

    var user: User?
        set(value) {
            prefs.user = value
        }
        get() = prefs.user
}