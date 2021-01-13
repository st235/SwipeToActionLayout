package github.com.st235.lib_swipetoactionlayout.behaviour

import android.content.Context

internal class BehaviourDelegatesFactory(
    private val context: Context
) {

    fun create(actionCount: Int): BehaviourDelegate {
//        return LeftDirectedBehaviourDelegate(actionCount)
        return FullLeftDirectedBehaviorDelegate(actionCount, context)
    }

}