package app.emmabritton.cibusana.internal

import app.emmabritton.cibusana.RuntimeTest
import app.emmabritton.cibusana.flow.home.HomeState
import app.emmabritton.cibusana.flow.login.LoginState
import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.flow.welcome.WelcomeState
import app.emmabritton.cibusana.internal.scopes.HomeScope
import app.emmabritton.cibusana.internal.scopes.LoginScope
import app.emmabritton.cibusana.internal.scopes.SplashScope
import app.emmabritton.cibusana.internal.scopes.WelcomeScope
import app.emmabritton.cibusana.system.UiState

interface ScopeFactory<T, C: UiState> {
    val name: String
    val uiState: UiState
    val testClass: Class<C>
    fun makeScope(runtimeScope: RuntimeTest.RuntimeScope): T
}

object Splash : ScopeFactory<SplashScope, SplashState> {
    override val name = "splash"
    override val uiState = SplashState
    override val testClass = SplashState::class.java
    override fun makeScope(runtimeScope: RuntimeTest.RuntimeScope) =
        SplashScope(runtimeScope)
}

object Login : ScopeFactory<LoginScope, LoginState> {
    override val name = "login"
    override val uiState = LoginState.Entering.init()
    override val testClass = LoginState::class.java
    override fun makeScope(runtimeScope: RuntimeTest.RuntimeScope) =
        LoginScope(runtimeScope)
}

object Home : ScopeFactory<HomeScope, HomeState> {
    override val name = "home"
    override val uiState = HomeState
    override val testClass = HomeState::class.java
    override fun makeScope(runtimeScope: RuntimeTest.RuntimeScope) =
        HomeScope(runtimeScope)
}

object Welcome : ScopeFactory<WelcomeScope, WelcomeState> {
    override val name = "welcome"
    override val uiState = WelcomeState
    override val testClass = WelcomeState::class.java
    override fun makeScope(runtimeScope: RuntimeTest.RuntimeScope) =
        WelcomeScope(runtimeScope)
}