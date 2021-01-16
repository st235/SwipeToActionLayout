package github.com.st235.lib_swipetoactionlayout.events

import github.com.st235.lib_swipetoactionlayout.QuickActionsStates

internal class QuickActionsMenuStateProcessor {

    var onProgressiveStateChanged: ((state: QuickActionsStates) -> Unit)? = null
    var onReleaseStateChanged: ((state: QuickActionsStates) -> Unit)? = null

    private var state: QuickActionsStates? = null

    fun setState(state: QuickActionsStates) {
        val oldState = this.state
        val newState = state

        if (oldState != newState) {
            onProgressiveStateChanged?.invoke(newState)
            this.state = newState
        }
    }

    fun release() {
        state?.let { onReleaseStateChanged?.invoke(it) }
    }

}