package app.emmabritton.cibusana.system

import app.emmabritton.system.ActionReceiver
import app.emmabritton.system.Command
import app.emmabritton.system.CommandException
import app.emmabritton.system.CommandHandler
import timber.log.Timber
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadedCommandHandler : CommandHandler {
    private val executor =
        ThreadPoolExecutor(1, 4, 60L, TimeUnit.SECONDS, SynchronousQueue())

    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        Timber.tag(TAG).d("Queuing ${command.javaClass.simpleName}")
        executor.submit {
            try {
                Timber.tag(TAG).d("Executing ${command.javaClass.simpleName}")
                command.run(actionReceiver)
            } catch (e: Exception) {
                actionReceiver.receive(CommandException(command.javaClass.simpleName, e))
            }
        }
    }

    companion object {
        private const val TAG = "[CH]"
    }
}