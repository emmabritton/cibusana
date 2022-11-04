package app.emmabritton.cibusana.system

import android.os.Handler
import android.os.Looper
import app.emmabritton.system.*

typealias AppEffect = Effect<AppState>

val androidMainThreadMarshaller: Marshaller =
    { method: () -> Unit -> Handler(Looper.getMainLooper()).post { method() } }

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
)

