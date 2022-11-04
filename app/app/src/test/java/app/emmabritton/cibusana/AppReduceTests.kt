package app.emmabritton.cibusana

import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.system.AppState
import org.junit.Test

class AppReduceTests {
    @Test
    fun `check init state is Splash`() {
        val state = AppState.init()

        state.assertNoGlobalError()

        state.assertUiState(SplashState::class.java)
    }
}