package github.com.st235.lib_swipetoactionlayout.behaviour

import android.view.View
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.Size

internal class NoOpBehaviourDelegate: BehaviourDelegate {

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Size) {
        // empty on purpose
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Size): Int {
        // empty on purpose
        return 0
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Size, dx: Int, index: Int) {
        // empty on purpose
    }

    override fun isOpened(position: Int, actionSize: Size): Boolean {
        // empty on purpose
        return false
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Size): Int {
        // empty on purpose
        return 0
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Size
    ): QuickActionsStates {
        // empty on purpose
        return QuickActionsStates.CLOSED
    }

    override fun getPositionForState(view: View, actionSize: Size, states: QuickActionsStates): Int {
        // empty on purpose
        return 0
    }
}