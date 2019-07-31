package github.com.st235.lib_swipetoactionlayout

import android.view.View

internal interface ActionViewObserver {
    fun onInit(width: Int, height: Int, actions: List<SwipeAction>) { }
    fun onMeasure(width: Int, height: Int, actions: List<SwipeAction>, views: List<View>, gravity: SwipeActionActionViewFactory.Gravity)
    fun onLayout(width: Int, height: Int, actions: List<SwipeAction>, views: List<View>, gravity: SwipeActionActionViewFactory.Gravity)
}