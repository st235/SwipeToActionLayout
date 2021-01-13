package github.com.st235.lib_swipetoactionlayout

import android.view.View

interface OnSwipeListener {
    fun onClosed(view: View)
    fun onOpened(view: View)
    fun onSlide(view: View, slideOffset: Float)
}
