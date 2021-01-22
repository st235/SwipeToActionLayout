package github.com.st235.lib_swipetoactionlayout.behaviour

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import androidx.core.animation.addListener
import github.com.st235.lib_swipetoactionlayout.ActionFactory
import github.com.st235.lib_swipetoactionlayout.QuickActionsStates
import github.com.st235.lib_swipetoactionlayout.utils.clamp
import github.com.st235.lib_swipetoactionlayout.utils.max
import github.com.st235.lib_swipetoactionlayout.utils.min

internal class FullLeftDirectedBehaviorDelegate(
    private val actionCount: Int,
    private val context: Context
): LeftDirectedBehaviourDelegate(actionCount, context) {

    enum class States {
        CLOSED,
        IS_CLOSING,
        OPENED,
        IS_OPENING
    }

    private var state = States.CLOSED
    private var originalRight: Float = 0F
    private var lastRightPosition: Float = 0F

    private var animation: ValueAnimator? = null

    override fun layoutAction(view: View, l: Int, r: Int, actionSize: Int) {
        if (!ActionFactory.isLast(view)) {
            super.layoutAction(view, l, r, actionSize)
        } else {
            view.translationX = 0F
            val parentWidth = r - l
            view.layout(l - parentWidth, 0, l, actionSize)
        }
    }

    override fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Int): Int {
        return clamp(left, 0, parentView.measuredWidth)
    }

    override fun translateAction(mainView: View, actionView: View, actionSize: Int, dx: Int, index: Int) {
        val distance = actionSize * actionCount
        val distanceFill = clamp(mainView.left / distance.toFloat(), 0F, 1F)

        if (!ActionFactory.isLast(actionView)) {
            actionView.translationX = clamp(
                (distanceFill * distance * ((actionCount - index + 1).toFloat() / actionCount)),
                0F,
                distance * (actionCount - index + 1).toFloat() / actionCount
            )
        }

        if (ActionFactory.isLast(actionView)) {
            if (isFullyOpened(mainView, actionSize) && (state != States.IS_OPENING && state != States.OPENED)) {
                animation?.cancel()

                state = States.IS_OPENING
                animation = ValueAnimator.ofFloat(0F, 1F)
                originalRight = actionView.translationX

                val startingPosition = actionView.translationX

                animation?.addUpdateListener {
                    val progress = (it.animatedValue as Float)
                    val distance = mainView.left - startingPosition
                    actionView.translationX = startingPosition + distance * progress
                }

                animation?.duration = 250L

                animation?.addListener(onEnd = {
                    this.animation = null
                    lastRightPosition = actionView.translationX
                    state = States.OPENED
                }, onCancel = {
                    this.animation = null
                    state = States.OPENED
                })

                animation?.start()
            } else if (isFullyOpened(mainView, actionSize) && (state == States.OPENED)) {
                actionView.translationX = mainView.left.toFloat()
                lastRightPosition = actionView.translationX
            } else if (!isFullyOpened(mainView, actionSize) && (state != States.IS_CLOSING && state != States.CLOSED)) {
                animation?.cancel()

                state = States.IS_CLOSING
                animation = ValueAnimator.ofFloat(1F, 0F)

                animation?.addUpdateListener {
                    val progress = (it.animatedValue as Float)
                    val finalPoint = min(originalRight, actionView.translationX)
                    val distance = max(lastRightPosition - finalPoint, 0F)
                    actionView.translationX = originalRight + progress * distance
                    lastRightPosition = actionView.translationX
                }

                animation?.addListener(onEnd = {
                    this.animation = null
                    state = States.CLOSED
                }, onCancel = {
                    this.animation = null
                    actionView.translationX = originalRight
                    state = States.CLOSED
                })

                animation?.start()
            } else {
                val finalOrigin = clamp(
                    (distanceFill * distance * ((actionCount - index + 1).toFloat() / actionCount)),
                    0F,
                    distance * (actionCount - index + 1).toFloat() / actionCount
                )

                if (state == States.IS_CLOSING || state == States.IS_OPENING) {
                    originalRight = finalOrigin
                } else {
                    actionView.translationX = finalOrigin
                }
            }
        }
    }

    override fun isOpened(position: Int, actionSize: Int): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize * actionCount
        return position > (translateDistance / 2) && position <= translateDistance
    }

    override fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Int): Int {
        val translateDistance = actionSize * actionCount
        val isFastFling = velocityHelper.shouldBeConsideredAsFast(velocity)

        return when {
            isFullyOpened(view, actionSize) -> view.measuredWidth
            isFastFling && velocityHelper.isRight(velocity) -> translateDistance
            isFastFling && velocityHelper.isLeft(velocity) -> 0
            isOpened(view.left, actionSize) -> translateDistance
            else -> 0
        }
    }

    private fun isFullyOpened(view: View, actionSize: Int): Boolean {
        // position is the left side of a view
        // as we can move view only to left position belongs to [-translateDistance, 0]
        val translateDistance = actionSize * actionCount
        val position = view.left
        return position > translateDistance
    }

    override fun getStateForPosition(
        view: View,
        actionSize: Int
    ): QuickActionsStates {
        return if (isFullyOpened(view, actionSize)) {
            QuickActionsStates.FULL_OPENED
        } else if (isOpened(view.left, actionSize)) {
            QuickActionsStates.OPENED
        } else {
            QuickActionsStates.CLOSED
        }
    }

    override fun gePositionForState(view: View, actionSize: Int, states: QuickActionsStates): Int {
        return when(states) {
            QuickActionsStates.FULL_OPENED -> view.measuredWidth
            QuickActionsStates.OPENED -> actionSize * actionCount
            QuickActionsStates.CLOSED -> 0
        }
    }

}