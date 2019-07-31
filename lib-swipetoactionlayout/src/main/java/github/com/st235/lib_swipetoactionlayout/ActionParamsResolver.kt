package github.com.st235.lib_swipetoactionlayout

import android.text.TextPaint
import android.util.TypedValue
import androidx.annotation.Dimension
import androidx.annotation.IntRange
import androidx.annotation.Px
import github.com.st235.lib_swipetoactionlayout.utils.toPx

internal class ActionParamsResolver {

    internal data class ActionInfo(
        @Dimension(unit = Dimension.SP) val textSize: Float,
        @Dimension(unit = Dimension.PX) val actionWidth: Int,
        @Dimension(unit = Dimension.PX) val actionHeight: Int,
        @IntRange(from = 1, to = 2) val maxLines: Int,
        val shouldTruncateText: Boolean = false
    )

    companion object {
        val EMPTY_INFO = ActionInfo(
            textSize = 14F,
            actionHeight = 0,
            actionWidth = 0,
            maxLines = 1
        )
    }

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

    fun obtainActionInfoFor(
        maxWidth: Int,
        optimalActionWidth: Int,
        optimalActionHeight: Int,
        @Dimension(unit = Dimension.DP) horizontalMargins: Float,
        actions: List<SwipeAction>
    ): ActionInfo {
        var maxTextWidth = 0

        var doesAllActionsFits = true

        for (action in actions) {
            val currentTextWidth =
                measureTextWidthWithMargins(text = action.text, textSize = 14.0F, horizontalMargins = horizontalMargins)

            if (currentTextWidth > optimalActionWidth) {
                doesAllActionsFits = false
            }

            if (currentTextWidth > maxTextWidth) {
                maxTextWidth = currentTextWidth
            }
        }

        if (doesAllActionsFits) {
            return ActionInfo(14.0F, optimalActionWidth, optimalActionHeight, maxLines = 1)
        }

        if (maxTextWidth * actions.size <= maxWidth) {
            return ActionInfo(14.0F, maxTextWidth, optimalActionHeight, maxLines = 1)
        }

        return ActionInfo(
            14.0F,
            Math.min((maxTextWidth / 1.5).toInt(), maxWidth / actions.size),
            optimalActionHeight,
            2,
            true
        )
    }

    @Px
    private fun measureTextWidthWithMargins(
        text: CharSequence,
        @Dimension(unit = Dimension.SP) textSize: Float,
        @Dimension(unit = Dimension.DP) horizontalMargins: Float = 2.0F
    ): Int {
        return Math.round(measureTextWidth(text, textSize) + 2 * horizontalMargins.toPx())
    }

    @Px
    private fun measureTextWidth(
        text: CharSequence,
        @Dimension(unit = Dimension.SP) textSize: Float
    ): Int {
        textPaint.textSize = textSize.toPx(units = TypedValue.COMPLEX_UNIT_SP)
        return Math.round(textPaint.measureText(text.toString()))
    }
}