package app.emmabritton.system

/**
 * Represents something that is going to happen, such as SubmitFoodSearch, Logout
 */
interface Command {
    fun run(actionReceiver: ActionReceiver)
}