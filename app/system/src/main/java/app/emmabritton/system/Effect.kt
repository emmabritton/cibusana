package app.emmabritton.system

/**
 * After reducing an action, this contains the new state and any commands that need to be executed
 */
open class Effect<S: State>(val newState: S, val commands: List<Command>)