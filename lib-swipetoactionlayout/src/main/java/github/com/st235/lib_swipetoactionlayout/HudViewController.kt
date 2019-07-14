package st235.com.swipeablecontainer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewAnimationUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


class HudViewController(
    context: Context,
    private val swipableActionViewFactory: SwipableActionViewFactory
) {

    private val hudView: ViewGroup = FrameLayout(context)

    private lateinit var leftActionView: View
    private lateinit var rightActionView: View

    fun attachToParent(parent: ViewGroup, firstAction: SwipableAction) {
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        hudView.setBackgroundColor(Color.TRANSPARENT)
        parent.addView(hudView, lp)


        val desiredSize = Math.min(parent.width, parent.height)
        leftActionView = swipableActionViewFactory.create(
            action = firstAction,
            desiredWidth = desiredSize,
            desiredHeight = desiredSize,
            gravity = SwipableActionViewFactory.Gravity.LEFT
        )

        leftActionView.visibility = View.INVISIBLE

        rightActionView = swipableActionViewFactory.create(
            action = firstAction,
            desiredWidth = desiredSize,
            desiredHeight = desiredSize,
            gravity = SwipableActionViewFactory.Gravity.RIGHT
        )

        rightActionView.visibility = View.INVISIBLE

        hudView.addView(leftActionView, leftActionView.layoutParams)
        hudView.addView(rightActionView, rightActionView.layoutParams)
    }

    fun hide() {
        hudView.visibility = View.INVISIBLE
    }

    fun hideAt(actionView: View, direction: SwipeToActionLayout.Direction) {
        when(direction) {
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

        val reversedReveal = createRevealAt(action = actionView, isReversed = true)
        reversedReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                hudView.visibility = View.INVISIBLE
            }
        })

        reversedReveal.start()
    }

    fun revealAt(actionView: View, direction: SwipeToActionLayout.Direction) {
        when(direction) {
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
        val reveal = createRevealAt(actionView)

        reveal.start()
    }

    private fun createRevealAt(action: View, isReversed: Boolean = false): Animator {
        val x = (action.right + action.left) / 2
        val y = (action.top + action.bottom) / 2

        val endRadius = Math.hypot(hudView.width.toDouble(), hudView.height.toDouble()).toInt()

        val reveal =
        if (isReversed) {
            ViewAnimationUtils.createCircularReveal(hudView, x, y, endRadius.toFloat(), 0F)
        } else {
            ViewAnimationUtils.createCircularReveal(hudView, x, y, 0F, endRadius.toFloat())
        }

        reveal.duration = 250L

        return reveal
    }
}
