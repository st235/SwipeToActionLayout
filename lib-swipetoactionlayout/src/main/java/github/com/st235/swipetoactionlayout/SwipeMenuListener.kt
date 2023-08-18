package github.com.st235.swipetoactionlayout

import android.view.View

interface SwipeMenuListener {
    fun onClosed(view: View)
    fun onOpened(view: View)
    fun onFullyOpened(view: View, quickAction: SwipeAction)

    fun onActionClicked(view: View, action: SwipeAction)
}
