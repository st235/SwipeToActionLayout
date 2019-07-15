package github.com.st235.lib_swipetoactionlayout

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SwipeAction(
    @ColorInt val color: Int,
    @DrawableRes val iconId: Int,
    @StringRes val text: Int,
    @ColorInt val iconTint: Int = Color.WHITE,
    @ColorInt val textColor: Int = Color.WHITE
)
