package github.com.st235.lib_swipetoactionlayout.behaviour

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.addListener
import github.com.st235.lib_swipetoactionlayout.utils.Size

internal class LastActionStateController(
    private val delegate: Delegate
) {

    private enum class State {
        CLOSED,
        IS_CLOSING,
        OPENED,
        IS_OPENING
    }

    open class AnimatorListener {
        open fun onUpdate(animator: ValueAnimator) {}
        open fun onEnd() {}
        open fun onCancel() {}
    }

    interface Delegate {

        fun isFullyOpened(view: View, actionSize: Size): Boolean

        fun createOpeningAnimation(
            mainView: View,
            actionView: View,
            actionSize: Size
        ): AnimatorListener

        fun createClosingAnimation(
            mainView: View,
            actionView: View,
            actionSize: Size
        ): AnimatorListener

        fun onLastActionFullMove(mainView: View, actionView: View)

        fun onCrossInteractionMove(isAnimatedState: Boolean, mainView: View, actionView: View, actionSize: Size, index: Int)

    }

    private var state = State.CLOSED
    private var animation: Animator? = null

    fun onTranslate(mainView: View, actionView: View, actionSize: Size, dx: Int, index: Int) {
        when {
            isFullyOpened(mainView, actionSize) && !isOpeningOrOpened() -> {
                cancelAllPossibleAnimation()
                state = State.IS_OPENING
                animation = createAnimation(delegate.createOpeningAnimation(mainView, actionView, actionSize), State.OPENED)
                animation?.start()
            }
            !isFullyOpened(mainView, actionSize) && !isClosingOrClosed() -> {
                cancelAllPossibleAnimation()
                state = State.IS_CLOSING
                animation = createAnimation(delegate.createClosingAnimation(mainView, actionView, actionSize), State.CLOSED)
                animation?.start()
            }
            isFullyOpened(mainView, actionSize) && isOpened() -> {
                delegate.onLastActionFullMove(mainView, actionView)
            }
            else -> {
                delegate.onCrossInteractionMove(isAnimatedState(), mainView, actionView, actionSize, index)
            }
        }
    }

    private fun createAnimation(listener: AnimatorListener, terminationState: State): Animator {
        val animator = ValueAnimator.ofFloat(0F, 1F)

        animator.addUpdateListener {
            listener.onUpdate(it)
        }

        animator.addListener(
            onEnd = {
                this.animation = null
                this.state = terminationState
                listener.onEnd()
            },
            onCancel = {
                this.animation = null
                this.state = terminationState
                listener.onCancel()
            }
        )

        return animator
    }

    private fun isFullyOpened(view: View, actionSize: Size): Boolean {
        return delegate.isFullyOpened(view, actionSize)
    }

    private fun isOpeningOrOpened(): Boolean {
        return state == State.IS_OPENING || state == State.OPENED
    }

    private fun isOpened(): Boolean {
        return state == State.OPENED
    }

    private fun isClosingOrClosed(): Boolean {
        return state == State.IS_CLOSING || state == State.CLOSED
    }

    private fun isAnimatedState(): Boolean {
        return state == State.IS_CLOSING || state == State.IS_OPENING
    }

    private fun cancelAllPossibleAnimation() {
        animation?.cancel()
        animation = null
    }

}