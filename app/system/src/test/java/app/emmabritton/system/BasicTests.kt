package app.emmabritton.system

import org.junit.Assert.assertEquals
import org.junit.Test

class BasicTests {
    @Test
    fun `check action functionality`() {
        data class TestState(val isLoggedIn: Boolean) : State
        class Login : Action

        val reduce: (Action, TestState) -> Effect<TestState> = { action: Action, state: TestState ->
            when (action) {
                is Login -> Effect(state.copy(isLoggedIn = true), emptyList())
                else -> throw IllegalArgumentException("Unhandled action: ${action.describe()} ($action)")
            }
        }

        val kernel = createTestRuntime(reduce, TestState(false))
        kernel.receive(Login())
        assert(kernel.state().isLoggedIn)
    }

    @Test
    fun `check command functionality`() {
        data class TestState(val count: Int) : State
        class TestSubmitIncCount : Action
        class IncrementCount : Action
        class IncCount : Command {
            override fun run(actionReceiver: ActionReceiver) {
                actionReceiver.receive(IncrementCount())
            }
        }

        val reduce: (Action, TestState) -> Effect<TestState> = { action: Action, state: TestState ->
            when (action) {
                is TestSubmitIncCount -> Effect(state, listOf(IncCount()))
                is IncrementCount -> Effect(state.copy(count = state.count + 1), emptyList())
                else -> throw IllegalArgumentException("Unhandled action: ${action.describe()} ($action)")
            }
        }

        val kernel = createTestRuntime(reduce, TestState(0))

        kernel.receive(TestSubmitIncCount())
        assertEquals(kernel.state().count, 1)

        kernel.receive(TestSubmitIncCount())
        assertEquals(kernel.state().count, 2)
    }
}