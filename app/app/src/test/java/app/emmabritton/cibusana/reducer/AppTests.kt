package app.emmabritton.cibusana.reducer

import app.emmabritton.cibusana.flow.splash.State
import app.emmabritton.cibusana.internal.assertUiState
import app.emmabritton.cibusana.system.AppState
import org.junit.Test

class AppTests {
    @Test
    fun `check init state is Splash`() {
        val state = AppState.init()

        state.assertUiState(State::class.java)
    }
}