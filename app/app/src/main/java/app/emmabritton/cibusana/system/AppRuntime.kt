package app.emmabritton.cibusana.system

import android.os.Handler
import android.os.Looper
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
    override fun receive(action: Action) {
        Timber.tag(TAG).d("Received ${action.describe()} during $state")
        super.receive(action)
    }

    fun goBack(): Boolean {
        return state.uiState.previousState?.let {
            receive(GoToState(state, it))
            true
        } ?: false
    }

    companion object {
        private const val TAG = "[RT]"
    }
}

