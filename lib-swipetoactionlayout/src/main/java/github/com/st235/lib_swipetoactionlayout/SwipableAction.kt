package st235.com.swipeablecontainer

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

data class SwipableAction(
    @ColorInt val color: Int,
    @DrawableRes val iconId: Int,
    @StringRes val text: Int,
    @ColorInt val textColor: Int = Color.WHITE
)
