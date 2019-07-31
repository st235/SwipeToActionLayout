package github.com.st235.lib_swipetoactionlayout

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import github.com.st235.lib_swipetoactionlayout.ActionParamsResolver.Companion.EMPTY_INFO
import github.com.st235.lib_swipetoactionlayout.utils.toPx

internal class SwipeActionActionViewFactory(
    private val swipeToActionLayout: SwipeToActionLayout,
    private val onActionClickListener: OnActionClickListener
) : ActionViewObserver {

    companion object {
        const val HORIZONTAL_MARGIN_IN_DP = 8F
    }

    @SuppressLint("RtlHardcoded")
    enum class Gravity(internal var androidGravity: Int) {
        LEFT(androidGravity = android.view.Gravity.LEFT),
        RIGHT(androidGravity = android.view.Gravity.RIGHT)
    }

    private val context = swipeToActionLayout.context
    private val actionParamsResolver = ActionParamsResolver()
    internal var lastKnownActionInfo = EMPTY_INFO

    fun createLayout(actions: List<SwipeAction>, gravity: Gravity): List<View> {
        val views = mutableListOf<View>()

        if (gravity == Gravity.LEFT) {
            for (i in (actions.size - 1) downTo 0) {
                val actionView = create(actions[i], gravity)
                swipeToActionLayout.addView(actionView, actionView.layoutParams)

                views.add(0, actionView)
            }
        }

        if (gravity == Gravity.RIGHT) {
            for (i in 0 until actions.size) {
                val actionView = create(actions[i], gravity)
                swipeToActionLayout.addView(actionView, actionView.layoutParams)

                views.add(actionView)
            }
        }

        return views
    }

    override fun onInit(width: Int, height: Int, actions: List<SwipeAction>) {
        val desiredSize = Math.min(width, height)
        lastKnownActionInfo = actionParamsResolver.obtainActionInfoFor(
            (width * 0.9F).toInt(),
            Math.min(desiredSize, height / actions.size), desiredSize, HORIZONTAL_MARGIN_IN_DP, actions
        )
    }

    override fun onMeasure(
        width: Int,
        height: Int,
        actions: List<SwipeAction>,
        views: List<View>,
        gravity: Gravity
    ) {
        if (gravity == Gravity.LEFT) {
            for (i in (actions.size - 1) downTo 0) {
                val actionView = views[i]
                actionView.measure(
                    View.MeasureSpec.makeMeasureSpec(lastKnownActionInfo.actionWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(lastKnownActionInfo.actionHeight, View.MeasureSpec.EXACTLY)
                )
            }
        }

        if (gravity == Gravity.RIGHT) {
            for (i in 0 until actions.size) {
                val actionView = views[i]
                actionView.measure(
                    View.MeasureSpec.makeMeasureSpec(lastKnownActionInfo.actionWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(lastKnownActionInfo.actionHeight, View.MeasureSpec.EXACTLY)
                )
            }
        }
    }

    override fun onLayout(
        width: Int,
        height: Int,
        actions: List<SwipeAction>,
        views: List<View>,
        gravity: Gravity
    ) {
        var margin = (actions.size - 1) * lastKnownActionInfo.actionWidth
        if (gravity == Gravity.LEFT) {
            for (i in (actions.size - 1) downTo 0) {
                val actionView = views[i]
                actionView.layout(margin, 0, margin + lastKnownActionInfo.actionWidth, height)
                margin -= lastKnownActionInfo.actionWidth
            }
        }

        margin = (actions.size - 1) * lastKnownActionInfo.actionWidth

        if (gravity == Gravity.RIGHT) {
            for (i in 0 until actions.size) {
                val actionView = views[i]
                actionView.layout(width - margin - lastKnownActionInfo.actionWidth, 0, width - margin, height)
                margin -= lastKnownActionInfo.actionWidth
            }
        }
    }

    internal fun create(
        action: SwipeAction,
        gravity: Gravity
    ): View {
        val actionView = LinearLayout(context)
        actionView.orientation = LinearLayout.VERTICAL
        actionView.setVerticalGravity(android.view.Gravity.CENTER_VERTICAL)
        actionView.setHorizontalGravity(android.view.Gravity.CENTER_HORIZONTAL)
        actionView.isFocusable = true
        actionView.isClickable = true
        actionView.setBackgroundColor(action.color)
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
        actionView.addView(createTextView(action, lastKnownActionInfo), textLayoutParams)

        val actionLayoutParams =
            FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        actionLayoutParams.leftMargin = 0
        actionLayoutParams.rightMargin = 0
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

    private fun createTextView(
        action: SwipeAction,
        actionInfo: ActionParamsResolver.ActionInfo
    ): View {
        val textView = TextView(context)
        textView.text = action.text
        textView.setTextColor(action.textColor)
        textView.textSize = actionInfo.textSize
        textView.maxLines = actionInfo.maxLines
        textView.gravity = android.view.Gravity.CENTER
        textView.setLineSpacing(0F, 0.9F)

        if (actionInfo.shouldTruncateText) {
            textView.ellipsize = TextUtils.TruncateAt.END
        }

        return textView
    }
}