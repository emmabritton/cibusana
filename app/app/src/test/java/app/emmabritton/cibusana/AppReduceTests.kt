package app.emmabritton.cibusana

import app.emmabritton.cibusana.system.AppState
import app.emmabritton.cibusana.system.welcome.WelcomeState
import org.junit.Test

class AppReduceTests {
    @Test
    fun `check init state is Welcome`() {
        val state = AppState.init()

        state.assertNoGlobalError()

        state.assertUiState(WelcomeState::class.java)
    }
}