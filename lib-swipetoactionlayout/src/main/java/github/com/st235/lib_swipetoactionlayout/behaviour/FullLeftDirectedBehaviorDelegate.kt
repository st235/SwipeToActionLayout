package github.com.st235.lib_swipetoactionlayout.behaviour

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import androidx.core.animation.addListener
import github.com.st235.lib_swipetoactionlayout.ActionFactory
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.Size
import github.com.st235.lib_swipetoactionlayout.utils.clamp
import github.com.st235.lib_swipetoactionlayout.utils.max
import github.com.st235.lib_swipetoactionlayout.utils.min

internal class FullLeftDirectedBehaviorDelegate(
    private val actionCount: Int,
    private val context: Context
): LeftDirectedBehaviourDelegate(actionCount, context) {


    private inner class LastActionDelegate : LastActionStateController.Delegate {

        private var originalRight: Float = 0F
        private var lastRightPosition: Float = 0F

        override fun isFullyOpened(view: View, actionSize: Size): Boolean {
            return this@FullLeftDirectedBehaviorDelegate.isFullyOpened(view, actionSize)
        }

        override fun createOpeningAnimation(
            mainView: View,
            actionView: View,
            actionSize: Size
        ): LastActionStateController.AnimatorListener {
            originalRight = actionView.translationX

            return object : LastActionStateController.AnimatorListener() {

                val startingPosition = actionView.translationX

                override fun onUpdate(animator: ValueAnimator) {
                    val progress = (animator.animatedValue as Float)
                    val distance = mainView.left - startingPosition
                    actionView.translationX = startingPosition + distance * progress
                }

                override fun onEnd() {
                    lastRightPosition = actionView.translationX
                }
            }
        }

        override fun createClosingAnimation(
            mainView: View,
            actionView: View,
            actionSize: Size
        ): LastActionStateController.AnimatorListener {
            return object : LastActionStateController.AnimatorListener() {

                override fun onUpdate(animator: ValueAnimator) {
                    val progress = 1F - (animator.animatedValue as Float)
                    val finalPoint = min(originalRight, actionView.translationX)
                    val distance = max(lastRightPosition - finalPoint, 0F)
                    actionView.translationX = originalRight + progress * distance
                    lastRightPosition = actionView.translationX
                }

                override fun onCancel() {
                    actionView.translationX = originalRight
                }
            }
        }

        override fun onLastActionFullMove(mainView: View, actionView: View) {
            actionView.translationX = mainView.left.toFloat()
            lastRightPosition = actionView.translationX
        }

        override fun onCrossInteractionMove(
            isAnimatedState: Boolean,
            mainView: View,
            actionView: View,
            actionSize: Size,
            index: Int
        ) {
            val distance = actionSize.width * actionCount
            val distanceFill = clamp(mainView.left / distance.toFloat(), 0F, 1F)

            val finalOrigin = clamp(
                (distanceFill * distance * ((actionCount - index + 1).toFloat() / actionCount)),
                0F,
                distance * (actionCount - index + 1).toFloat() / actionCount
            )

            if (isAnimatedState) {
                originalRight = finalOrigin
            } else {
                actionView.translationX = finalOrigin
            }
        }
    }

    private val lastActionStateController = LastActionStateController(LastActionDelegate())

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Size) {
        if (!ActionFactory.isLast(view)) {
            super.layoutAction(view, l, r, actionSize)
        } else {
            view.translationX = 0F
            val parentWidth = r - l
            view.layout(l - parentWidth, 0, l, actionSize.height)
        }
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Size): Int {
        return clamp(left, 0, parentView.measuredWidth)
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Size, dx: Int, index: Int) {
        if (!ActionFactory.isLast(actionView)) {
            val distance = actionSize.width * actionCount
            val distanceFill = clamp(mainView.left / distance.toFloat(), 0F, 1F)

            actionView.translationX = clamp(
                (distanceFill * distance * ((actionCount - index + 1).toFloat() / actionCount)),
                0F,
                distance * (actionCount - index + 1).toFloat() / actionCount
            )
        } else {
            lastActionStateController.onTranslate(mainView, actionView, actionSize, dx, index)
        }
    }

    override fun isOpened(position: Int, actionSize: Size): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize.width * actionCount
        return position > (translateDistance / 2) && position <= translateDistance
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Size): Int {
        val translateDistance = actionSize.width * actionCount
        val isFastFling = velocityHelper.shouldBeConsideredAsFast(velocity)

        return when {
            isFullyOpened(view, actionSize) -> view.measuredWidth
            isFastFling && velocityHelper.isRight(velocity) -> translateDistance
            isFastFling && velocityHelper.isLeft(velocity) -> 0
            isOpened(view.left, actionSize) -> translateDistance
            else -> 0
        }
    }

    private fun isFullyOpened(view: View, actionSize: Size): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize.width * actionCount
        val position = view.left
        return position > translateDistance
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Size
    ): QuickActionsStates {
        return if (isFullyOpened(view, actionSize)) {
            QuickActionsStates.FULL_OPENED
        } else if (isOpened(view.left, actionSize)) {
            QuickActionsStates.OPENED
        } else {
            QuickActionsStates.CLOSED
        }
    }

    override fun getPositionForState(view: View, actionSize: Size, states: QuickActionsStates): Int {
        return when(states) {
            QuickActionsStates.FULL_OPENED -> view.measuredWidth
            QuickActionsStates.OPENED -> actionSize.width * actionCount
            QuickActionsStates.CLOSED -> 0
        }
    }

}