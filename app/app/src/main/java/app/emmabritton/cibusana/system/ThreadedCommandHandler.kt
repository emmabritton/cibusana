package app.emmabritton.cibusana.system

import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import app.emmabritton.system.CommandException
import app.emmabritton.system.CommandHandler
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadedCommandHandler : CommandHandler {
    private val executor =
        ThreadPoolExecutor(1, 4, 60L, TimeUnit.SECONDS, SynchronousQueue())

    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        executor.submit {
            try {
                command.run(actionReceiver)
            } catch (e: Exception) {
                actionReceiver.receive(CommandException(command.javaClass.simpleName, e))
            }
        }
    }
}