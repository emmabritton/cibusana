package app.emmabritton.cibusana.persist

import android.content.Context
import app.emmabritton.cibusana.network.Logger
import app.emmabritton.cibusana.persist.models.User

interface Prefs {
    var user: User?
}

class AndroidPrefs(context: Context, private val logger: Logger) : Prefs {
    private val prefs = context.getSharedPreferences("common", Context.MODE_PRIVATE)

    override var user: User?
        get() {
            return if (prefs.contains(KEY_USER_NAME) && prefs.contains(KEY_USER_TOKEN)) {
                User(prefs.getString(KEY_USER_NAME, "")!!, prefs.getString(KEY_USER_TOKEN, "")!!)
            } else {
                null
            }
        }
        set(value) {
            if (value == null) {
                logger.d("Clearing user data")
                prefs.edit()
                    .putString(KEY_USER_NAME, null)
                    .putString(KEY_USER_TOKEN, null)
                    .apply()
            } else {
                logger.d("Storing user data for ${value.name}")
                prefs.edit()
                    .putString(KEY_USER_NAME, value.name)
                    .putString(KEY_USER_TOKEN, value.token)
                    .apply()
            }
        }

    companion object {
        private const val KEY_USER_NAME = "name.string"
        private const val KEY_USER_TOKEN = "token.string"
    }
}