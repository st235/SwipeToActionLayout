package github.com.st235.lib_swipetoactionlayout

import android.view.View

interface MenuListener {
    fun onClosed(view: View)
    fun onOpened(view: View)
    fun onFullyOpened(view: View)

    fun onActionClicked(view: View, action: SwipeAction)
}
