package app.emmabritton.system

interface CommandHandler {
    var actionReceiver: ActionReceiver

    fun add(command: Command)
}

class ImmediateCommandHandler : CommandHandler {
    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        command.run(actionReceiver)
    }

}