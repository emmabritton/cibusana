package app.emmabritton.system

private val runOnMainThread = { method: () -> Unit -> method() }

class TestRuntimeKernel<S : State>(
    reduce: (Action, S) -> Effect<S>,
    initState: S,
    render: (S) -> Unit = {}
) : RuntimeKernel<S>(runOnMainThread, reduce, render, ImmediateCommandHandler(), initState) {
    init {
        commandHandler.actionReceiver = this
    }
}

fun <S : State> createTestRuntime(
    reduce: (Action, S) -> Effect<S>,
    initState: S,
    render: (S) -> Unit = {}
) = TestRuntimeKernel(reduce, initState, render)
