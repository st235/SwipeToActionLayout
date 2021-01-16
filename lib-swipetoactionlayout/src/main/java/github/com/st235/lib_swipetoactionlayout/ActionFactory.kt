package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import github.com.st235.lib_swipetoactionlayout.utils.show

internal class ActionFactory(
    private val context: Context
) {

    internal companion object {
        private data class Payload(val isLast: Boolean)

        @JvmStatic
        fun markAsAction(v: View, isLast: Boolean) {
            v.tag = Payload(isLast = isLast)
        }

        @JvmStatic
        fun isAction(v: View): Boolean {
            return v.tag is Payload
        }

        @JvmStatic
        fun isLast(v: View): Boolean {
            return (v.tag as? Payload)?.isLast == true
        }

    }

    fun createAction(item: SwipeAction, isLast: Boolean, gravity: Int): View {
        val container = LinearLayout(context)
        container.id = item.actionId
        container.orientation = LinearLayout.VERTICAL

        val iconView = ImageView(context)
        val titleView = TextView(context)

        container.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL

        iconView.setImageResource(item.iconId)
        iconView.setColorFilter(item.iconTint, PorterDuff.Mode.SRC_ATOP)

        titleView.text = item.text
        titleView.setTextColor(item.textColor)
        titleView.gravity = Gravity.CENTER
        titleView.show(isShown = item.text != null)

        container.addView(iconView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        container.addView(titleView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val finalContainer = if (isLast) wrap(container, gravity) else container

        if (item.background is ColorDrawable) {
            finalContainer.background = item.background
        } else {
            container.background = item.background
        }

        markAsAction(finalContainer, isLast)

        return finalContainer
    }

    private fun wrap(view: ViewGroup, gravity: Int): ViewGroup {
        val frameLayout = FrameLayout(context)

        frameLayout.addView(view, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            gravity
        ))

        return frameLayout
    }

}