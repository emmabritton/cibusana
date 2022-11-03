package app.emmabritton.system

import org.jetbrains.annotations.TestOnly

typealias Marshaller = (() -> Unit) -> Unit

/**
 * The core of the library, it receives actions and reduces to produce commands,
 * they are passed to the CommandHandler with executes generating more actions,
 * and so on..
 */
open class RuntimeKernel<S: State>(
    /**
     * Executes code on the main thread
     *
     * Primarily used so Android code doesn't attempt to alter the UI from a background thread
     */
    private val runOnMainThread: Marshaller,
    /**
     * Converts state and an action into a new state with optional commands
     */
    private val reduce: (Action, S) -> Effect<S>,
    /**
     * Called after an action has been reduced with the new state
     */
    private val render: (S) -> Unit,
    private val commandHandler: CommandHandler,
    initState: S
) : ActionReceiver {
    private var state = initState

    init {
        commandHandler.actionReceiver = this
    }

    @TestOnly
    fun state(): S = state

    @Synchronized
    override fun receive(action: Action) {
        runOnMainThread {
            val effect = reduce(action, state)
            state = effect.newState
            for (command in effect.commands) {
                commandHandler.add(command)
            }
            render(state)
        }
    }
}