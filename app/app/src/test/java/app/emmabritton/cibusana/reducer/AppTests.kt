package app.emmabritton.cibusana.reducer

import app.emmabritton.cibusana.assertUiState
import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.system.AppState
import org.junit.Test

class AppTests {
    @Test
    fun `check init state is Splash`() {
        val state = AppState.init()

        state.assertUiState(SplashState::class.java)
    }
}