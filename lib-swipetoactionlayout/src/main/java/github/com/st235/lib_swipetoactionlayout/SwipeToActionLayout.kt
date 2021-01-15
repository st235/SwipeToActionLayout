package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import github.com.st235.lib_swipetoactionlayout.behaviour.BehaviourDelegate
import github.com.st235.lib_swipetoactionlayout.behaviour.BehaviourDelegatesFactory
import github.com.st235.lib_swipetoactionlayout.behaviour.NoOpBehaviourDelegate
import github.com.st235.lib_swipetoactionlayout.utils.isLtr
import github.com.st235.lib_swipetoactionlayout.utils.max
import github.com.st235.lib_swipetoactionlayout.utils.min


internal typealias SwipeToActionLayoutLayoutParams = ViewGroup.MarginLayoutParams

class SwipeToActionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    enum class MenuGravity {

        LEFT {
            override fun getViewGravity(): Int {
                return Gravity.RIGHT
            }
        },
        RIGHT {
            override fun getViewGravity(): Int {
                return Gravity.LEFT
            }
        },
        START {
            override fun getViewGravity(): Int {
                return if (isLtr()) {
                    Gravity.RIGHT
                } else {
                    Gravity.LEFT
                }
            }
        },
        END {
            override fun getViewGravity(): Int {
                return if (isLtr()) {
                    Gravity.LEFT
                } else {
                    Gravity.RIGHT
                }
            }
        };

        internal abstract fun getViewGravity(): Int

    }

    var actions: List<SwipeAction> = mutableListOf()
        set(value) {
            if (value.isEmpty()) {
                throw IllegalArgumentException("Items list cannot be null")
            }

            field = listOf(*value.toTypedArray())
            reloadActions()
        }

    var gravity: MenuGravity = MenuGravity.RIGHT
    set(value) {
        field = value
        reloadActions()
    }

    var isFullActionSupported: Boolean = false
    set(value) {
        field = value
        reloadActions()
    }

    private val actionFactory = ActionFactory(context)
    private val behaviourDelegateFactory = BehaviourDelegatesFactory(context)

    private var actionSize = 0

    private var delegate: BehaviourDelegate = NoOpBehaviourDelegate()
    private val viewDragHelper = ViewDragHelper.create(this, ViewDragHelperCallback())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var contentWidth = 0
        var contentHeight = 0

        var actionWidth = 0
        var actionHeight = 0

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            if (childView.visibility == View.GONE) {
                continue
            }

            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0)

            if (ActionFactory.isAction(childView)) {
                actionWidth = max(actionWidth, childView.measuredWidth)
                actionHeight = max(actionHeight, childView.measuredHeight)
                continue
            }

            contentWidth = max(contentWidth, childView.measuredWidth)
            contentHeight = max(contentHeight, childView.measuredHeight)
        }

        actionSize = max(actionWidth, contentHeight /* action should be the same size as content */)

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            if (childView.visibility == View.GONE) {
                continue
            }

            if (!ActionFactory.isAction(childView)) {
                continue
            }

            childView.measure(
                MeasureSpec.makeMeasureSpec(actionSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(actionSize, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(
            desiredSize(widthMeasureSpec, contentWidth),
            desiredSize(heightMeasureSpec, contentHeight)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            if (childView.visibility == View.GONE) {
                childView.layout(0, 0, 0, 0)
                continue
            }

            if (ActionFactory.isAction(childView)) {
                delegate.layoutAction(childView, l, r, actionSize)
            } else {
                childView.layout(0, 0, childView.measuredWidth, childView.measuredHeight)
            }
        }
    }

    private fun desiredSize(measureSpec: Int, size: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val availableSize = MeasureSpec.getSize(measureSpec)

        return when(mode) {
            MeasureSpec.EXACTLY -> availableSize
            MeasureSpec.AT_MOST -> min(availableSize, size)
            MeasureSpec.UNSPECIFIED -> size
            else -> size
        }
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is SwipeToActionLayoutLayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return SwipeToActionLayoutLayoutParams(
            SwipeToActionLayoutLayoutParams.WRAP_CONTENT,
            SwipeToActionLayoutLayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return SwipeToActionLayoutLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return SwipeToActionLayoutLayoutParams(p)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return super.onInterceptTouchEvent(ev)
        }

        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        viewDragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun reloadActions() {
        val items = actions

        removeAllActions()

        delegate = behaviourDelegateFactory.create(items.size, gravity, isFullActionSupported)

        for ((index, item) in items.withIndex()) {
            val isLastItem = (index == items.lastIndex)
            val associatedView = actionFactory.createAction(item, isLastItem, gravity.getViewGravity())
            addView(associatedView)
        }

        requestLayout()
    }

    private fun removeAllActions() {
        val removeCandidates = mutableListOf<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (ActionFactory.isAction(child)) {
                removeCandidates.add(child)
            }
        }

        for (child in removeCandidates) {
            removeView(child)
        }
    }

    private inner class ViewDragHelperCallback: ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return !ActionFactory.isAction(child)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return delegate.clampViewPosition(this@SwipeToActionLayout, child, left, actionSize)
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            var actionOrder = 1
            for (i in 0 until childCount) {
                val childView = getChildAt(i)

                if (ActionFactory.isAction(childView)) {
                    delegate.translateAction(changedView, childView, actionSize, dx, actionOrder)
                    actionOrder++
                }
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val finalLeftPosition = delegate.getFinalLeftPosition(releasedChild, xvel, actionSize)
            viewDragHelper.settleCapturedViewAt(finalLeftPosition, 0)
            invalidate()
        }
    }
}