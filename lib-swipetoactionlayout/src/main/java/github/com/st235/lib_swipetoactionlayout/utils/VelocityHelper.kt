package github.com.st235.lib_swipetoactionlayout.utils

import android.content.Context
import android.view.ViewConfiguration
import kotlin.math.abs

internal class VelocityHelper(
    private val context: Context
) {

    private companion object {
        const val FLING_THRESHOLD = 0.1F
    }

    fun isLeft(velocity: Float): Boolean {
        return velocity < 0
    }

    fun isRight(velocity: Float): Boolean {
        return velocity > 0
    }

    fun shouldBeConsideredAsFast(velocity: Float): Boolean {
        val maxVelocityFling = ViewConfiguration.get(context).scaledMaximumFlingVelocity.toFloat()
        val percentX = velocity / maxVelocityFling
        return abs(percentX) >= FLING_THRESHOLD
    }

}