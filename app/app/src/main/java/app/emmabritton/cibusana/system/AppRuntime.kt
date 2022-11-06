package app.emmabritton.cibusana.system

import android.os.Handler
import android.os.Looper
import app.emmabritton.cibusana.flow.reduce
import app.emmabritton.system.*
import timber.log.Timber

typealias AppEffect = Effect<AppState>

val androidMainThreadMarshaller: Marshaller =
    { method: () -> Unit ->
        if (Looper.myLooper() == Looper.getMainLooper()) {
            method()
        } else {
            Handler(Looper.getMainLooper()).post { method() }
        }
    }

class Runtime(
    marshaller: Marshaller = androidMainThreadMarshaller,
    commandHandler: CommandHandler = ThreadedCommandHandler(),
    render: (AppState) -> Unit
) : RuntimeKernel<AppState>(
    marshaller,
    ::reduce,
    render,
    commandHandler,
    AppState.init()
) {
    init {
        commandHandler.actionReceiver = this

        postStateChange = {
            if (state.uiState.config.clearUiHistory) {
                Timber.tag(TAG).i("Clearing History")
                state = state.copy(uiHistory = emptyList())
            }
            if (state.uiState.config.addToHistory) {
                Timber.tag(TAG).i("Adding ${state.uiState.javaClass.simpleName} to history")
                val isSameClass =
                    state.uiHistory.lastOrNull()?.javaClass == state.uiState.javaClass
                if (isSameClass && state.uiState.config.replaceDuplicate) {
                    Timber.tag(TAG).i("Removing duplicate")
                    state = state.copy(uiHistory = state.uiHistory.dropLast(1))
                }
                state = state.copy(uiHistory = state.uiHistory + state.uiState)
            }
        }
    }

    override fun receive(action: Action) {
        Timber.tag(TAG).d("Received ${action.describe()} during $state")
        if (action is GoBack) {
            //TODO: cancel commands
            Timber.tag(TAG).i("Going back (dropping ${state.uiHistory.take(state.uiHistory.count() - 2).joinToString()})")
            state = state.copy(uiState = action.state, uiHistory = state.uiHistory.take(state.uiHistory.count() - 2))
            render(state)
        } else {
            super.receive(action)
        }
    }

    /**
     * Returns true if runtime has handled back pressed (and so the activity should do nothing)
     * If false the activity should finish
     */
    fun onBackPressed(): Boolean {
        Timber.tag(TAG).d("Back pressed")
        return synchronized(stateChangeLock) {
            val config = state.uiState.config
            if (config.isBackEnabled) {
                if (state.uiHistory.count() <= 1) {
                    Timber.tag(TAG).i("Finishing as stack is empty")
                    false
                } else {
                    Timber.tag(TAG).i("Restoring to previous screen")
                    val lastTwo = state.uiHistory.takeLast(2)
                    receive(GoBack(lastTwo[0]))
                    true
                }
            } else {
                Timber.tag(TAG).i("Ignored as back disabled")
                true
            }
        }
    }

    companion object {
        private const val TAG = "[RT]"
    }
}
