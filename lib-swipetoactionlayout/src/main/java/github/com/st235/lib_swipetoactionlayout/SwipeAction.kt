package github.com.st235.lib_swipetoactionlayout

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class SwipeAction(
    @IdRes val actionId: Int,
    val background: Drawable?,
    @DrawableRes val iconId: Int,
    val text: CharSequence?,
    @ColorInt val iconTint: Int = Color.WHITE,
    @ColorInt val textColor: Int = Color.WHITE
)
