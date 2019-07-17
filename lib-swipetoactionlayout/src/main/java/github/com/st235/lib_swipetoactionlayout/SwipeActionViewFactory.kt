package github.com.st235.lib_swipetoactionlayout

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import st235.com.swipeablecontainer.toPx

internal class SwipeActionViewFactory(private val swipeToActionLayout: SwipeToActionLayout,
                             private val onActionClickListener: OnActionClickListener
) {

    @SuppressLint("RtlHardcoded")
    enum class Gravity(internal var androidGravity: Int) {
        LEFT(androidGravity = android.view.Gravity.LEFT),
        RIGHT(androidGravity = android.view.Gravity.RIGHT)
    }

    private val context = swipeToActionLayout.context

    private val viewBounds = Rect()

    fun onOwnerBoundsChanged(newWidth: Int, newHeight: Int) {
        viewBounds.set(0, 0, newWidth, newHeight)
    }

    fun createLayout(actions: List<SwipeAction>, gravity: Gravity): List<View> {
        val views = mutableListOf<View>()
        val desiredSize = Math.min(viewBounds.width(), viewBounds.height())

        var margin = (actions.size - 1) * desiredSize

        if (gravity == Gravity.LEFT) {
            for (i in (actions.size - 1) downTo 0) {
                val action = actions[i]

                val actionView = create(action, desiredSize, desiredSize, margin, 0, gravity)
                swipeToActionLayout.addView(actionView, actionView.layoutParams)

                views.add(0, actionView)

                margin -= desiredSize
            }
        }

        margin = (actions.size - 1) * desiredSize

        if (gravity == Gravity.RIGHT) {
            for (i in 0 until actions.size) {
                val action = actions[i]

                val actionView = create(action, desiredSize, desiredSize, 0, margin, gravity)
                swipeToActionLayout.addView(actionView, actionView.layoutParams)

                views.add(actionView)
                actionView.isClickable = true
                actionView.isFocusable = true

                margin -= desiredSize
            }
        }

        return views
    }

    internal fun create(
            action: SwipeAction,
            desiredWidth: Int,
            desiredHeight: Int,
            marginLeft: Int = 0,
            marginRight: Int = 0,
            gravity: Gravity
    ): View {
        val actionView = LinearLayout(context)
        actionView.orientation = LinearLayout.VERTICAL
        actionView.setVerticalGravity(android.view.Gravity.CENTER_VERTICAL)
        actionView.setHorizontalGravity(android.view.Gravity.CENTER_HORIZONTAL)
        actionView.setBackgroundColor(action.color)
        actionView.isFocusable = true
        actionView.isClickable = true
        actionView.setOnClickListener { onActionClickListener(actionView, action) }

        val iconLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        val textLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        textLayoutParams.topMargin = 4.toPx()

        actionView.addView(createIconView(action), iconLayoutParams)
        actionView.addView(createTextView(action), textLayoutParams)

        val actionLayoutParams = FrameLayout.LayoutParams(desiredWidth, desiredHeight)
        actionLayoutParams.leftMargin = marginLeft
        actionLayoutParams.rightMargin = marginRight
        actionLayoutParams.gravity = gravity.androidGravity

        actionView.layoutParams = actionLayoutParams

        return actionView
    }

    private fun createIconView(action: SwipeAction): View {
        val iconView = AppCompatImageView(context)
        iconView.setImageResource(action.iconId)
        iconView.setColorFilter(action.iconTint, PorterDuff.Mode.SRC_ATOP);
        return iconView
    }

    private fun createTextView(action: SwipeAction): View {
        val textView = TextView(context)
        textView.setText(action.text)
        textView.setTextColor(action.textColor)
        textView.textSize = 14F
        textView.gravity = android.view.Gravity.CENTER
        return textView
    }
}