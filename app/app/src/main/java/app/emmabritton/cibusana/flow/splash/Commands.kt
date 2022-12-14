package app.emmabritton.cibusana.flow.splash

import app.emmabritton.cibusana.flow.home.HomeAction
import app.emmabritton.cibusana.flow.welcome.WelcomeAction
import app.emmabritton.cibusana.persist.DataController
import app.emmabritton.cibusana.persist.UserController
import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
 * Load user data from storage
 */
class LoadUserData : Command {
    private val userController: UserController by KoinJavaComponent.inject(UserController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val user = userController.user
        if (user != null) {
            actionReceiver.receive(HomeAction.UserLoggedIn(user))
        } else {
            actionReceiver.receive(WelcomeAction.Show)
        }
    }
}

/**
 * Load all data from server into memory (less than 1MB)
 */
class PreLoadCacheData : Command {
    private val dataController: DataController by KoinJavaComponent.inject(DataController::class.java)

    override fun run(actionReceiver: ActionReceiver) {
        val cats = dataController.getCategories().fold({it.size.toString()},{"Error"}).let { "Categories: $it" }
        val companies = dataController.getCompanies().fold({it.size.toString()},{"Error"}).let { "Companies: $it" }
        val flags = dataController.getFlags().fold({it.size.toString()},{"Error"}).let { "Flags: $it" }
        val allergens = dataController.getAllergens().fold({it.size.toString()},{"Error"}).let { "Allergens: $it" }
        val flavors = dataController.getFlavors().fold({it.size.toString()},{"Error"}).let { "Flavours: $it" }
        val cuisines = dataController.getCuisines().fold({it.size.toString()},{"Error"}).let { "Cuisines: $it" }
        val times = dataController.getMealTimes().fold({it.size.toString()},{"Error"}).let { "Times: $it" }
        val results = listOf(cats, companies, flags, allergens, flavors, cuisines, times).joinToString("\n")
        Timber.d("Preload complete with: \n$results")
    }
}