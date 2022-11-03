package app.emmabritton.system

/**
 * Receives actions
 *
 * This only used so RuntimeKernel can be passed around without exposing it's other methods
 */
interface ActionReceiver {
    fun receive(action: Action)
}