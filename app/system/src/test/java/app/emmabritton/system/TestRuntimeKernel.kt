package app.emmabritton.system

private val runOnMainThread = { method: () -> Unit -> method() }

fun <S : State> createTestRuntime(
    reduce: (Action, S) -> Effect<S>,
    initState: S,
    render: (S) -> Unit = {}
): RuntimeKernel<S> {
    return RuntimeKernel(runOnMainThread, reduce, render, ImmediateCommandHandler(), initState)
}
