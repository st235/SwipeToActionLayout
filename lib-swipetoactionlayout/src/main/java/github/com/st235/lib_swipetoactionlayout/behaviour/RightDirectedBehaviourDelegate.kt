package github.com.st235.lib_swipetoactionlayout.behaviour

import android.content.Context
import android.view.View
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.Size
import github.com.st235.lib_swipetoactionlayout.utils.VelocityHelper
import github.com.st235.lib_swipetoactionlayout.utils.clamp

internal open class RightDirectedBehaviourDelegate(
    private val actionCount: Int,
    private val context: Context
): BehaviourDelegate {

    protected val velocityHelper = VelocityHelper(context)

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Size) {
        //reset view translation on relayout
        view.translationX = 0F
        view.layout(r, 0, r + actionSize.width, actionSize.height)
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Size): Int {
        return clamp(left, -actionSize.width * actionCount, 0)
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Size, dx: Int, index: Int) {
        actionView.translationX += (dx * ((actionCount - index + 1).toFloat() / actionCount))
    }

    override fun isOpened(position: Int, actionSize: Size): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize.width * actionCount
        return position < (-translateDistance / 2)
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Size): Int {
        val translateDistance = actionSize.width * actionCount
        val isFastFling = velocityHelper.shouldBeConsideredAsFast(velocity)

        return when {
            isFastFling && velocityHelper.isLeft(velocity) -> -translateDistance
            isFastFling && velocityHelper.isRight(velocity) -> 0
            isOpened(view.left, actionSize) -> -translateDistance
            else -> 0
        }
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Size
    ): QuickActionsStates {
        return if (isOpened(view.left, actionSize)) {
            QuickActionsStates.OPENED
        } else {
            QuickActionsStates.CLOSED
        }
    }

    override fun getPositionForState(view: View, actionSize: Size, states: QuickActionsStates): Int {
        return when(states) {
            QuickActionsStates.FULL_OPENED -> throw IllegalArgumentException("Unsupported state")
            QuickActionsStates.OPENED -> -(actionSize.width * actionCount)
            QuickActionsStates.CLOSED -> 0
        }
    }

}