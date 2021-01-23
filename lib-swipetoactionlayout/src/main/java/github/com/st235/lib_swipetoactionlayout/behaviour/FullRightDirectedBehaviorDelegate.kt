package github.com.st235.lib_swipetoactionlayout.behaviour

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import androidx.core.animation.addListener
import github.com.st235.lib_swipetoactionlayout.ActionFactory
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.clamp

internal class FullRightDirectedBehaviorDelegate(
    private val actionCount: Int,
    private val context: Context
) : RightDirectedBehaviourDelegate(actionCount, context) {

    private inner class LastActionDelegate : LastActionStateController.Delegate {

        override fun isFullyOpened(view: View, actionSize: Int): Boolean {
            return this@FullRightDirectedBehaviorDelegate.isFullyOpened(view, actionSize)
        }

        override fun createOpeningAnimation(
            mainView: View,
            actionView: View,
            actionSize: Int
        ): LastActionStateController.AnimatorListener {
            return object : LastActionStateController.AnimatorListener() {
                override fun onUpdate(animator: ValueAnimator) {
                    val percent = (animator.animatedValue as Float)
                    val distance = (actionView.left - mainView.right) + actionView.translationX
                    actionView.left = actionView.left - (distance * percent).toInt()
                }
            }
        }

        override fun createClosingAnimation(
            mainView: View,
            actionView: View,
            actionSize: Int
        ): LastActionStateController.AnimatorListener {
            return object : LastActionStateController.AnimatorListener() {

                private val finalViewPosition = mainView.measuredWidth

                override fun onUpdate(animator: ValueAnimator) {
                    val percent = (animator.animatedValue as Float)
                    val distance = (finalViewPosition - actionView.left)
                    actionView.left = (actionView.left + distance * percent).toInt()
                }

                override fun onCancel() {
                    actionView.left = finalViewPosition
                }
            }
        }

        override fun onLastActionFullMove(mainView: View, actionView: View) {
            actionView.left = (mainView.right - actionView.translationX).toInt()
        }

        override fun onCrossInteractionMove(isAnimatedState: Boolean, mainView: View, actionView: View, actionSize: Int, index: Int) {
            val distance = actionSize * actionCount
            val distanceFill = clamp(mainView.left / -distance.toFloat(), 0F, 1F)

            actionView.translationX =
                clamp(
                    (distanceFill * -distance * ((actionCount - index + 1).toFloat() / actionCount)),
                    -distance * (actionCount - index + 1).toFloat() / actionCount,
                    0F
                )
        }
    }

    private val lastActionStateController = LastActionStateController(LastActionDelegate())

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Int) {
        if (!ActionFactory.isLast(view)) {
            super.layoutAction(view, l, r, actionSize)
        } else {
            view.translationX = 0F
            val parentWidth = r - l
            view.layout(r, 0, r + parentWidth, actionSize)
        }
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Int): Int {
        return clamp(left, -parentView.measuredWidth, 0)
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Int, dx: Int, index: Int) {
        if (!ActionFactory.isLast(actionView)) {
            val distance = actionSize * actionCount
            val distanceFill = clamp(mainView.left / -distance.toFloat(), 0F, 1F)

            actionView.translationX =
                clamp(
                    (distanceFill * -distance * ((actionCount - index + 1).toFloat() / actionCount)),
                    -distance * (actionCount - index + 1).toFloat() / actionCount,
                    0F
                )
        } else {
            lastActionStateController.onTranslate(mainView, actionView, actionSize, dx, index)
        }
    }

    override fun isOpened(position: Int, actionSize: Int): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize * actionCount
        return position < (-translateDistance / 2) && position >= -translateDistance
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Int): Int {
        val translateDistance = actionSize * actionCount
        val isFastFling = velocityHelper.shouldBeConsideredAsFast(velocity)

        return when {
            isFullyOpened(view, actionSize) -> -view.measuredWidth
            isFastFling && velocityHelper.isLeft(velocity) -> -translateDistance
            isFastFling && velocityHelper.isRight(velocity) -> 0
            isOpened(view.left, actionSize) -> -translateDistance
            else -> 0
        }
    }

    private fun isFullyOpened(view: View, actionSize: Int): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize * actionCount
        val position = view.left
        return position < -translateDistance
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Int
    ): QuickActionsStates {
        return when {
            isFullyOpened(view, actionSize) -> {
                QuickActionsStates.FULL_OPENED
            }
            isOpened(view.left, actionSize) -> {
                QuickActionsStates.OPENED
            }
            else -> {
                QuickActionsStates.CLOSED
            }
        }
    }

    override fun gePositionForState(view: View, actionSize: Int, states: QuickActionsStates): Int {
        return when (states) {
            QuickActionsStates.FULL_OPENED -> -view.measuredWidth
            QuickActionsStates.OPENED -> -(actionSize * actionCount)
            QuickActionsStates.CLOSED -> 0
        }
    }

}