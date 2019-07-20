package github.com.st235.lib_swipetoactionlayout.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.Px

/**
 * Converts values to its real pixel size
 * using system density factor
 *
 * @return value in pixels
 */
@Px
internal fun Int.toPx(units: Int = TypedValue.COMPLEX_UNIT_DIP): Int {
    return TypedValue.applyDimension(
        units, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

/**
 * Converts values to its real pixel size
 * using system density factor
 *
 * @return value in pixels
 */
internal fun Float.toPx(units: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
    return TypedValue.applyDimension(
        units, this,
        Resources.getSystem().displayMetrics
    )
}
