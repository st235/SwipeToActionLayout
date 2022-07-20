package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.annotation.MenuRes
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import github.com.st235.lib_swipetoactionlayout.behaviour.BehaviourDelegate
import github.com.st235.lib_swipetoactionlayout.behaviour.BehaviourDelegatesFactory
import github.com.st235.lib_swipetoactionlayout.behaviour.NoOpBehaviourDelegate
import github.com.st235.lib_swipetoactionlayout.events.QuickActionsMenuStateProcessor
import github.com.st235.lib_swipetoactionlayout.parsers.XmlMenuParser
import github.com.st235.lib_swipetoactionlayout.utils.Size
import github.com.st235.lib_swipetoactionlayout.utils.isLtr
import github.com.st235.lib_swipetoactionlayout.utils.max
import github.com.st235.lib_swipetoactionlayout.utils.min

internal typealias SwipeToActionLayoutLayoutParams = ViewGroup.MarginLayoutParams

class SwipeToActionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    enum class MenuGravity(
        internal val id: Int
    ) {

        LEFT(0) {
            override fun getViewGravity(): Int {
                return Gravity.RIGHT
            }
        },
        RIGHT(1) {
            override fun getViewGravity(): Int {
                return Gravity.LEFT
            }
        },
        START(2) {
            override fun getViewGravity(): Int {
                return if (isLtr()) {
                    Gravity.RIGHT
                } else {
                    Gravity.LEFT
                }
            }
        },
        END(3) {
            override fun getViewGravity(): Int {
                return if (isLtr()) {
                    Gravity.LEFT
                } else {
                    Gravity.RIGHT
                }
            }
        };

        internal abstract fun getViewGravity(): Int

        internal companion object {
            fun findById(id: Int): MenuGravity =
                values().find { it.id == id } ?: throw IllegalArgumentException("Cannot find value with id: $id")
        }
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

    var shouldVibrateOnQuickAction: Boolean = false

    private val actionFactory = ActionFactory(context)
    private val behaviourDelegateFactory = BehaviourDelegatesFactory(context)

    private var inProgressStateProcessor = QuickActionsMenuStateProcessor(
        initState = QuickActionsStates.CLOSED
    )

    private var actionSize: Size = Size(0, 0)

    private var delegate: BehaviourDelegate = NoOpBehaviourDelegate()
    private val viewDragHelper = ViewDragHelper.create(this, ViewDragHelperCallback())

    var menuListener: SwipeMenuListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeToActionLayout)

        gravity = MenuGravity.findById(typedArray.getInt(R.styleable.SwipeToActionLayout_sal_gravity, MenuGravity.RIGHT.id))
        isFullActionSupported = typedArray.getBoolean(R.styleable.SwipeToActionLayout_sal_isFullActionSupported, false)
        shouldVibrateOnQuickAction = typedArray.getBoolean(R.styleable.SwipeToActionLayout_sal_shouldVibrateOnQuickAction, false)

        val menuId = typedArray.getResourceId(R.styleable.SwipeToActionLayout_sal_items, View.NO_ID)
        if (menuId != View.NO_ID) {
            val barParser = XmlMenuParser(context)
            val items = barParser.inflate(menuId)
            actions = items
        }

        typedArray.recycle()

        inProgressStateProcessor.onReleaseStateChangedListener = { state ->
            when (state) {
                QuickActionsStates.FULL_OPENED -> {
                    menuListener?.onFullyOpened(this, actions.last())
                }
                else -> {
                    // empty on purpose
                }
            }

        }

        inProgressStateProcessor.onProgressStateChangedListener = { state ->
            when (state) {
                QuickActionsStates.FULL_OPENED -> {
                    if (shouldVibrateOnQuickAction) {
                        performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                }
                QuickActionsStates.OPENED -> {
                    menuListener?.onOpened(this)
                }
                QuickActionsStates.CLOSED -> {
                    menuListener?.onClosed(this)
                }
            }
        }
    }

    fun setActionsRes(@MenuRes menuRes: Int) {
        val barParser = XmlMenuParser(context)
        val items = barParser.inflate(menuRes)
        actions = items
        reloadActions()
    }

    fun close() {
        transitionToState(QuickActionsStates.CLOSED)
    }

    fun open() {
        transitionToState(QuickActionsStates.OPENED)
    }

    fun fullyOpen() {
        transitionToState(QuickActionsStates.FULL_OPENED)
    }

    private fun transitionToState(state: QuickActionsStates) {
        val position = delegate.getPositionForState(this, actionSize, state)
        if (viewDragHelper.smoothSlideViewTo(findContentView(), position, 0)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        assertOneChildOnly()

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

        val maxAvailableWidth = desiredSize(widthMeasureSpec, contentWidth)
        val actionCount = actions.size

        val size = max(actionWidth, contentHeight /* action should be the same size as content */)
        actionSize.set(size, size)

        if (size * actionCount > 0.75 * maxAvailableWidth) {
            actionSize.set(((0.75F * maxAvailableWidth) / actionCount).toInt(), actionSize.height)
        }

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            if (childView.visibility == View.GONE) {
                continue
            }

            if (!ActionFactory.isAction(childView)) {
                continue
            }

            childView.measure(
                MeasureSpec.makeMeasureSpec(actionSize.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(actionSize.height, MeasureSpec.EXACTLY)
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

        return when (mode) {
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

        val shouldIntercept = viewDragHelper.shouldInterceptTouchEvent(ev)
        parent.requestDisallowInterceptTouchEvent(shouldIntercept)
        return shouldIntercept
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

    private fun assertOneChildOnly() {
        var count = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (!ActionFactory.isAction(child)) {
                count++

                if (count > 1) {
                    throw IllegalStateException("SwipeToActionLayout can handle only one direct child")
                }
            }
        }
    }

    private fun findContentView(): View {
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (!ActionFactory.isAction(child)) {
                return child
            }
        }

        throw IllegalStateException("Cannot find content view. There is should be one child.")
    }

    private fun reloadActions() {
        val items = actions

        removeAllActions()

        delegate = behaviourDelegateFactory.create(items.size, gravity, isFullActionSupported)

        for ((index, item) in items.withIndex()) {
            val isLastItem = (index == items.lastIndex)
            val associatedView =
                actionFactory.createAction(item, isLastItem, gravity.getViewGravity())

            associatedView.isClickable = true
            associatedView.isFocusable = true
            associatedView.setOnClickListener {
                menuListener?.onActionClicked(it, action = item)
            }

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

    private inner class ViewDragHelperCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return !ActionFactory.isAction(child)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            if (ActionFactory.isLast(child)) {
                return 0
            }

            return child.measuredWidth
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

            inProgressStateProcessor.setState(
                delegate.getStateForPosition(
                    changedView,
                    actionSize
                )
            )
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val finalLeftPosition = delegate.getFinalLeftPosition(releasedChild, xvel, actionSize)
            viewDragHelper.settleCapturedViewAt(finalLeftPosition, 0)
            inProgressStateProcessor.release()
            invalidate()
        }
    }
}