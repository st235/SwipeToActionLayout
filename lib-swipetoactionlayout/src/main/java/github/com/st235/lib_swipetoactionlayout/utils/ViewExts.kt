package github.com.st235.lib_swipetoactionlayout.utils

import android.view.View

internal fun View.show(isShown: Boolean = false) {
    visibility = if (isShown) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
