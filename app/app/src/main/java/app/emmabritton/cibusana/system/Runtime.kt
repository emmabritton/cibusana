package app.emmabritton.cibusana.system

import android.os.Handler
import android.os.Looper
import app.emmabritton.cibusana.system.login.LoginAction
import app.emmabritton.cibusana.system.login.reduceLoginAction
import app.emmabritton.system.*
import timber.log.Timber

typealias AppEffect = Effect<AppState>

class InvalidAction(msg: String) :
    AppEffect(AppState.init().copy(error = "Unknown action: $msg"), emptyList())

val androidMainThreadMarshaller:Marshaller = { method: () -> Unit -> Handler(Looper.getMainLooper()).post { method() } }

class Runtime(marshaller: Marshaller = androidMainThreadMarshaller, commandHandler: CommandHandler = ThreadedCommandHandler(), render: (AppState) -> Unit) : RuntimeKernel<AppState>(
    marshaller,
    ::reduce,
    render,
    commandHandler,
    AppState.init()
)

