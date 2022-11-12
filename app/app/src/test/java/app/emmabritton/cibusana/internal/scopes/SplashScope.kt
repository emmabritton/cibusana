package app.emmabritton.cibusana.internal.scopes

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.flow.splash.SplashAction

class SplashScope(private val scope: RuntimeTest.RuntimeScope) {
    fun init() {
        scope.enqueuePreloadData()
        scope.runtime.receive(SplashAction.InitialiseApp)
    }
}
