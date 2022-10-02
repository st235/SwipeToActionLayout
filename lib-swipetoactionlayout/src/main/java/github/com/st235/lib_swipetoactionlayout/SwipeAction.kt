package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

data class SwipeAction internal constructor(
    @IdRes val actionId: Int,
    val background: Drawable?,
    @DrawableRes val iconId: Int,
    val text: CharSequence?,
    @ColorInt val iconTint: Int,
    @ColorInt val textColor: Int
) {

    constructor(builder: Builder):
            this(
                builder.actionId,
                builder.background,
                builder.iconId,
                builder.text,
                builder.iconTint,
                builder.textColor
            )

    class Builder(
        @IdRes internal val actionId: Int = ViewCompat.generateViewId(),
        @DrawableRes internal val iconId: Int
    ) {

        internal var background: Drawable? = null
        internal var text: CharSequence? = null
        @ColorInt
        internal var iconTint: Int = Color.WHITE
        @ColorInt
        internal var textColor: Int = Color.WHITE

        fun background(background: Drawable): Builder {
            this.background = background
            return this
        }

        fun backgroundRes(context: Context, @DrawableRes backgroundRes: Int): Builder {
            this.background = ContextCompat.getDrawable(context, backgroundRes)
            return this
        }

        fun backgroundColor(@ColorInt color: Int): Builder {
            this.background = ColorDrawable(color)
            return this
        }

        fun text(text: CharSequence): Builder {
            this.text = text
            return this
        }

        fun iconTint(@ColorInt iconTint: Int): Builder {
            this.iconTint = iconTint
            return this
        }

        fun textColor(@ColorInt textColor: Int): Builder {
            this.textColor = textColor
            return this
        }

        fun build(): SwipeAction {
            return SwipeAction(this)
        }

    }

}
