package app.emmabritton.cibusana.system

import app.emmabritton.cibusana.flow.splash.SplashState
import app.emmabritton.cibusana.persist.models.User
import app.emmabritton.system.State

data class AppState(
    val error: String?,
    val uiState: UiState,
    /**
     * Contains all visited screens the user can return to
     * (also include the current screen, but this is ignored when going back)
     */
    val uiHistory: ArrayDeque<UiState>,
    val user: User?
) : State {
    override fun toString(): String {
        return if (error != null) {
            "Error: $error"
        } else {
            "UiState: ${uiState.javaClass.simpleName}, User: $user, History count: ${uiHistory.size}"
        }
    }

    companion object {
        fun init(): AppState {
            val uiState = SplashState
            return AppState(null, uiState, ArrayDeque(), null)
        }
    }
}


interface UiState {
    val config: UiStateConfig
}

data class UiStateConfig(
    /**
     * If false, pressing back is ignored
     */
    val isBackEnabled: Boolean,
    /**
     * If true, this clears the AppState.uiHistory when added
     */
    val clearUiHistory: Boolean,
    /**
     * If true, and the last entry in AppState.uiHistory is the same class
     * then this replaces it
     *
     * TODO: Consider ID based duplicate detection so FormScreen#1 can be edited and replace it's duplicated, then open
     * FormScreen#2 which can be edited, and then return to where #1 was left
     */
    val replaceDuplicate: Boolean,
    /**
     * If true, then this uiState will be added to AppState.uiHistory
     */
    val addToHistory: Boolean
) {
    companion object {
        /**
         * For screens that should be the first in the history, such a home page
         */
        fun originScreen() = UiStateConfig(true, true, true, true)
        /**
         * For screens that should not be remembered and block the back button
         * TODO: Change commands cancellable and this can deprecated in favor of tempScreen
         */
        fun loadingScreen() = UiStateConfig(false, false, true, false)
        /**
         * For any other screen
         */
        fun generalScreen() = UiStateConfig(true, false, true, true)
        /**
         * For screens that should not be remembered
         */
        fun tempScreen() = UiStateConfig(true, false, true, false)
    }
}

