package github.com.st235.lib_swipetoactionlayout

import java.util.*

class ActionBindHelper {

    private val actions: MutableMap<String, SwipeToActionLayout> = Collections.synchronizedMap(mutableMapOf())

    fun bind(id: String, swipeToActionLayout: SwipeToActionLayout) {
        val oldId = findWithView(swipeToActionLayout)
        oldId?.let { actions.remove(it) }
        actions[id] = swipeToActionLayout
    }

    private fun findWithView(swipeToActionLayout: SwipeToActionLayout): String? {
        for ((id, actionLayout) in actions.entries) {
            if (actionLayout == swipeToActionLayout) {
                return id
            }
        }

        return null
    }

    fun closeOtherThan(currentId: String) {
        for ((id, actionLayout) in actions.entries) {
            if (id == currentId) {
                continue
            }

            actionLayout.close()
        }
    }

}