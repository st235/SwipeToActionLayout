package github.com.st235.lib_swipetoactionlayout.events

import github.com.st235.lib_swipetoactionlayout.QuickActionsStates

internal class QuickActionsMenuStateProcessor(
    initState: QuickActionsStates? = null
) {

    var onProgressStateChangedListener: ((state: QuickActionsStates) -> Unit)? = null
    var onReleaseStateChangedListener: ((state: QuickActionsStates) -> Unit)? = null

    private var state: QuickActionsStates? = initState

    fun setState(state: QuickActionsStates) {
        val oldState = this.state
        val newState = state

        if (oldState != newState) {
            onProgressStateChangedListener?.invoke(newState)
            this.state = newState
        }
    }

    fun release() {
        state?.let { onReleaseStateChangedListener?.invoke(it) }
    }

}