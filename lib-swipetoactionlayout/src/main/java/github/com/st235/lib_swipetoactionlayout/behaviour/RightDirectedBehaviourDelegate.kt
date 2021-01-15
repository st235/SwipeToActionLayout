package github.com.st235.lib_swipetoactionlayout.behaviour

import android.content.Context
import android.graphics.Point
import android.view.View
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.clamp

internal open class RightDirectedBehaviourDelegate(
    private val actionCount: Int,
    private val context: Context
): BehaviourDelegate {

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Int) {
        //reset view translation on relayout
        view.translationX = 0F
        view.layout(r, 0, r + actionSize, actionSize)
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Int): Int {
        return clamp(left, -actionSize * actionCount, 0)
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Int, dx: Int, index: Int) {
        actionView.translationX += (dx * ((actionCount - index + 1).toFloat() / actionCount))
    }

    override fun isOpened(position: Int, actionSize: Int): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize * actionCount
        return position < (-translateDistance / 2)
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Int): Int {
        val translateDistance = actionSize * actionCount

        return if (isOpened(view.left, actionSize)) {
            -translateDistance
        } else {
            0
        }
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Int
    ): QuickActionsStates {
        return if (isOpened(view.left, actionSize)) {
            QuickActionsStates.OPENED
        } else {
            QuickActionsStates.CLOSED
        }
    }

    override fun gePositionForState(view: View, actionSize: Int, states: QuickActionsStates): Int {
        return when(states) {
            QuickActionsStates.FULL_OPENED -> throw IllegalArgumentException("Unsupported state")
            QuickActionsStates.OPENED -> -(actionSize * actionCount)
            QuickActionsStates.CLOSED -> 0
        }
    }

}