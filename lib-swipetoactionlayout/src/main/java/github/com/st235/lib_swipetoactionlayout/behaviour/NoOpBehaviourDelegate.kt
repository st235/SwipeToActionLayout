package github.com.st235.lib_swipetoactionlayout.behaviour

import android.view.View

internal class NoOpBehaviourDelegate: BehaviourDelegate {

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Int) {
        // empty on purpose
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Int): Int {
        // empty on purpose
        return 0
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Int, dx: Int, index: Int) {
        // empty on purpose
    }

    override fun isOpened(position: Int, actionSize: Int): Boolean {
        // empty on purpose
        return false
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Int): Int {
        // empty on purpose
        return 0
    }
}