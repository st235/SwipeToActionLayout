package github.com.st235.lib_swipetoactionlayout

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import github.com.st235.lib_swipetoactionlayout.utils.toPx

internal class SwipeActionViewFactory(
    private val swipeToActionLayout: SwipeToActionLayout,
    private val onActionClickListener: OnActionClickListener
) {

    val HORIZONTAL_MARGIN_IN_DP = 8F

    @SuppressLint("RtlHardcoded")
    enum class Gravity(internal var androidGravity: Int) {
        LEFT(androidGravity = android.view.Gravity.LEFT),
        RIGHT(androidGravity = android.view.Gravity.RIGHT)
    }

    private val context = swipeToActionLayout.context
    private val parentBounds = Rect()
    private val actionParamsResolver = ActionParamsResolver()
    internal lateinit var lastKnownActionInfo: ActionParamsResolver.ActionInfo

    fun onOwnerBoundsChanged(newWidth: Int, newHeight: Int) {
        parentBounds.set(0, 0, newWidth, newHeight)
    }

    fun createLayout(actions: List<SwipeAction>, gravity: Gravity): List<View> {
        val views = mutableListOf<View>()
        val desiredSize = Math.min(parentBounds.width(), parentBounds.height())
        lastKnownActionInfo = actionParamsResolver.obtainActionInfoFor(
            (parentBounds.width() * 0.9F).toInt(),
            Math.min(desiredSize, parentBounds.width() / actions.size), desiredSize, HORIZONTAL_MARGIN_IN_DP, actions
        )

        var margin = (actions.size - 1) * desiredSize

        if (gravity == Gravity.LEFT) {
            for (i in (actions.size - 1) downTo 0) {
                val action = actions[i]

                val actionView = create(action, lastKnownActionInfo, margin, 0, gravity)
                swipeToActionLayout.addView(actionView, actionView.layoutParams)

                views.add(0, actionView)

                margin -= desiredSize
            }
        }

        margin = (actions.size - 1) * desiredSize

        if (gravity == Gravity.RIGHT) {
            for (i in 0 until actions.size) {
                val action = actions[i]

                val actionView = create(action, lastKnownActionInfo, 0, margin, gravity)
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
        actionInfo: ActionParamsResolver.ActionInfo,
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

        val iconLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            32.toPx()
        )

        iconLayoutParams.topMargin = 8.toPx()
        textLayoutParams.leftMargin = HORIZONTAL_MARGIN_IN_DP.toPx().toInt()
        textLayoutParams.rightMargin = HORIZONTAL_MARGIN_IN_DP.toPx().toInt()

        actionView.addView(createIconView(action), iconLayoutParams)
        actionView.addView(createTextView(action, actionInfo), textLayoutParams)

        val actionLayoutParams = FrameLayout.LayoutParams(actionInfo.actionWidth, actionInfo.actionHeight)
        actionLayoutParams.leftMargin = marginLeft
        actionLayoutParams.rightMargin = marginRight
        actionLayoutParams.gravity = gravity.androidGravity

        actionView.layoutParams = actionLayoutParams

        return actionView
    }

    private fun createIconView(action: SwipeAction): View {
        val iconView = AppCompatImageView(context)
        iconView.setImageResource(action.iconId)
        iconView.setColorFilter(action.iconTint, PorterDuff.Mode.SRC_ATOP)
        return iconView
    }

    private fun createTextView(action: SwipeAction, actionInfo: ActionParamsResolver.ActionInfo): View {
        val textView = TextView(context)
        textView.text = action.text
        textView.setTextColor(action.textColor)
        textView.textSize = actionInfo.textSize
        textView.gravity = android.view.Gravity.CENTER
        textView.maxLines = actionInfo.maxLines
        textView.setLineSpacing(0F, 0.9F)

        if (actionInfo.shouldTruncateText) {
            textView.ellipsize = TextUtils.TruncateAt.END
        }

        return textView
    }
}