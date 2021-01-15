package github.com.st235.lib_swipetoactionlayout.utils

import android.content.Context
import android.util.LayoutDirection
import android.view.View
import androidx.core.text.TextUtilsCompat
import java.util.*

internal fun View.show(isShown: Boolean = false) {
    visibility = if (isShown) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

internal fun isLtr(): Boolean {
    return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.LTR
}
