package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat

data class SwipeAction internal constructor(
    @IdRes val actionId: Int,
    val background: Drawable?,
    @DrawableRes val iconId: Int,
    val text: CharSequence?,
    @ColorInt val iconTint: Int = Color.WHITE,
    @ColorInt val textColor: Int = Color.WHITE
) {

    companion object {

        fun withBackgroundColor(
            @IdRes actionId: Int,
            @DrawableRes iconId: Int,
            @ColorInt iconTint: Int = Color.WHITE,
            text: CharSequence?,
            @ColorInt textColor: Int = Color.WHITE,
            @ColorInt backgroundColor: Int
        ): SwipeAction {
            return SwipeAction(actionId, ColorDrawable(backgroundColor), iconId, text, iconTint, textColor)
        }

        fun withBackgroundColorRes(
            context: Context,
            @IdRes actionId: Int,
            @DrawableRes iconId: Int,
            @ColorInt iconTint: Int = Color.WHITE,
            text: CharSequence?,
            @ColorInt textColor: Int = Color.WHITE,
            @ColorRes backgroundColorRes: Int
        ): SwipeAction {
            val color = ContextCompat.getColor(context, backgroundColorRes)
            return SwipeAction(actionId, ColorDrawable(color), iconId, text, iconTint, textColor)
        }

        fun withBackgroundDrawable(
            @IdRes actionId: Int,
            @DrawableRes iconId: Int,
            @ColorInt iconTint: Int = Color.WHITE,
            text: CharSequence?,
            @ColorInt textColor: Int = Color.WHITE,
            background: Drawable?
        ): SwipeAction {
            return SwipeAction(actionId, background, iconId, text, iconTint, textColor)
        }

    }

}
