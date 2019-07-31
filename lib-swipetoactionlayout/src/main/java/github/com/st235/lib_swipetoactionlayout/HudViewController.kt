package github.com.st235.lib_swipetoactionlayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewAnimationUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


internal class HudViewController(
    context: Context,
    private val swipeActionViewFactory: SwipeActionActionViewFactory
): ActionViewObserver {

    private val hudView: ViewGroup = FrameLayout(context)

    private lateinit var leftActionView: View
    private lateinit var rightActionView: View

    fun attachToParent(parent: ViewGroup, action: SwipeAction) {
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        hudView.setBackgroundColor(Color.TRANSPARENT)
        parent.addView(hudView, lp)

        leftActionView = swipeActionViewFactory.create(
            action = action,
            gravity = SwipeActionActionViewFactory.Gravity.LEFT
        )

        leftActionView.visibility = View.INVISIBLE

        rightActionView = swipeActionViewFactory.create(
            action = action,
            gravity = SwipeActionActionViewFactory.Gravity.RIGHT
        )

        rightActionView.visibility = View.INVISIBLE

        hudView.addView(leftActionView, leftActionView.layoutParams)
        hudView.addView(rightActionView, rightActionView.layoutParams)
    }

    fun detachFromParent(parent: ViewGroup) {
        parent.removeView(hudView)
    }

    override fun onMeasure(
        width: Int,
        height: Int,
        actions: List<SwipeAction>,
        views: List<View>,
        gravity: SwipeActionActionViewFactory.Gravity
    ) {
        val lastActionInfo = swipeActionViewFactory.lastKnownActionInfo

        leftActionView.measure(
                View.MeasureSpec.makeMeasureSpec(lastActionInfo.actionWidth, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(lastActionInfo.actionHeight, View.MeasureSpec.EXACTLY)
        )

        rightActionView.measure(
            View.MeasureSpec.makeMeasureSpec(lastActionInfo.actionWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(lastActionInfo.actionHeight, View.MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(
        width: Int,
        height: Int,
        actions: List<SwipeAction>,
        views: List<View>,
        gravity: SwipeActionActionViewFactory.Gravity
    ) {
        val lastActionInfo = swipeActionViewFactory.lastKnownActionInfo

        hudView.layout(0, 0, width, height)

        leftActionView.layout(0, 0, lastActionInfo.actionWidth, lastActionInfo.actionHeight)
        rightActionView.layout(width - lastActionInfo.actionWidth, 0, width, lastActionInfo.actionHeight)
    }

    fun hideIfNeeded() {
        if (hudView.visibility != View.INVISIBLE) {
            hudView.visibility = View.INVISIBLE
        }
    }

    fun hideAt(actionView: View, direction: SwipeToActionLayout.Direction) {
        when (direction) {
            SwipeToActionLayout.Direction.RIGHT_TO_LEFT -> {
                leftActionView.visibility = View.VISIBLE
                rightActionView.visibility = View.INVISIBLE
            }
            SwipeToActionLayout.Direction.LEFT_TO_RIGHT -> {
                leftActionView.visibility = View.INVISIBLE
                rightActionView.visibility = View.VISIBLE
            }
        }

        hudView.setBackgroundColor((actionView.background as ColorDrawable).color)

        val reversedReveal = createRevealAt(action = actionView, isReversed = true, direction = direction)
        reversedReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                hudView.visibility = View.INVISIBLE
            }
        })

        reversedReveal.start()
    }

    fun revealAt(actionView: View, direction: SwipeToActionLayout.Direction) {
        when (direction) {
            SwipeToActionLayout.Direction.LEFT_TO_RIGHT -> {
                leftActionView.visibility = View.VISIBLE
                rightActionView.visibility = View.INVISIBLE
            }
            SwipeToActionLayout.Direction.RIGHT_TO_LEFT -> {
                leftActionView.visibility = View.INVISIBLE
                rightActionView.visibility = View.VISIBLE
            }
        }

        hudView.setBackgroundColor((actionView.background as ColorDrawable).color)

        hudView.visibility = View.VISIBLE
        val reveal = createRevealAt(action = actionView, direction = direction)

        reveal.start()
    }

    private fun createRevealAt(
        action: View,
        isReversed: Boolean = false,
        direction: SwipeToActionLayout.Direction
    ): Animator {
        val x = when {
            direction == SwipeToActionLayout.Direction.LEFT_TO_RIGHT && !isReversed -> action.left
            direction == SwipeToActionLayout.Direction.RIGHT_TO_LEFT && isReversed -> action.left
            direction == SwipeToActionLayout.Direction.RIGHT_TO_LEFT && !isReversed -> action.right
            direction == SwipeToActionLayout.Direction.LEFT_TO_RIGHT && isReversed -> action.right
            else -> (action.left + action.right) / 2
        }
        val y = (action.top + action.bottom) / 2

        val endRadius = Math.hypot(hudView.width.toDouble(), hudView.height.toDouble()).toInt()

        val reveal =
            if (isReversed) {
                ViewAnimationUtils.createCircularReveal(hudView, x, y, endRadius.toFloat(), 0F)
            } else {
                ViewAnimationUtils.createCircularReveal(hudView, x, y, 0F, endRadius.toFloat())
            }

        reveal.duration = 200L

        return reveal
    }
}
