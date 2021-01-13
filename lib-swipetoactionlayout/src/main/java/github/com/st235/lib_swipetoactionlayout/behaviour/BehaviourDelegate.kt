package github.com.st235.lib_swipetoactionlayout.behaviour

import android.graphics.Point
import android.view.View

internal interface BehaviourDelegate {

    fun layoutAction(view: View, l: Int, r: Int, actionSize: Int)

    fun clampViewPosition(parentView: View, view: View, left: Int, actionSize: Int): Int

    fun translateAction(mainView: View, actionView: View, actionSize: Int, dx: Int, index: Int)

    fun isOpened(position: Int, actionSize: Int): Boolean

    fun getFinalLeftPosition(view: View, velocity: Float, actionSize: Int): Int

}