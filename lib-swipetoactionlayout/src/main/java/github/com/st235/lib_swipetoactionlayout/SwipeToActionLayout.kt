package github.com.st235.lib_swipetoactionlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

typealias OnActionClickListener = (view: View, action: SwipeAction) -> Unit

class SwipeToActionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    internal enum class Direction {
        NONE,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT;

        companion object {
            fun obtainBy(distanceX: Float) = if (distanceX > 0F) RIGHT_TO_LEFT else LEFT_TO_RIGHT
        }
    }

    private enum class State {
        CLOSE,
        CLOSING,
        CLOSING_MANUALLY,
        OPEN,
        OPENING,
        DRAG_FULLY_OPENED
    }

    var isDragLocked = false

    val isOpened: Boolean
        get() = state == State.OPEN

    val isClosed: Boolean
        get() = state == State.CLOSE

    var onSwipeListener: OnSwipeListener? = null

    var onActionClickListener: OnActionClickListener? = null

    var onLongActionClickListener: OnActionClickListener? = null

    private lateinit var mainView: View
    private var lastKnownMainViewLeftPosition = 0

    private val swipeActionViewFactory = SwipeActionViewFactory(this) { v, a ->
        onActionClickListener?.invoke(v, a)
    }

    private val hudViewController = HudViewController(context = context, swipeActionViewFactory = swipeActionViewFactory)

    private val leftItems = mutableListOf<SwipeAction>()
    private val leftItemsViews = mutableListOf<View>()

    private val rightItems = mutableListOf<SwipeAction>()
    private val rightItemsViews = mutableListOf<View>()

    private val mGestureListener = SwipeToActionGestureListener()
    private val dragHelperCallback = SwipeToActionViewHelperCallback()

    private val viewClosedBounds = Rect()

    private var maxActionsWidth = 0

    @Px
    private val minDistRequestDisallowParent = 0

    private var doesOpenBeforeInit = false

    private var isAborted = false

    private var isScrolling = false

    private var state = State.CLOSE
    set(value) {
        if (value != field) {
            onStateChanged(value, field)
        }
        field = value
    }

    private var lastKnownSwipeDirection = Direction.NONE
    set(value) {
        if (value != field) {
            onDirectionChanged(value, field)
        }
        field = value
    }

    private var dragDist = 0f

    private var lastKnownTouchX = -1f
    private var dragHelper: ViewDragHelper
    private val gestureDetector: GestureDetectorCompat

    private val openedEdgePosition: Int
        get() = when (lastKnownSwipeDirection) {
                Direction.LEFT_TO_RIGHT -> viewClosedBounds.left + maxActionsWidth
                Direction.RIGHT_TO_LEFT -> viewClosedBounds.left - maxActionsWidth
                else -> 0
        }

    private val distToClosestEdge: Int
        get() = when (lastKnownSwipeDirection) {
                Direction.LEFT_TO_RIGHT -> {
                    val pivotRight = viewClosedBounds.left + maxActionsWidth

                    Math.min(
                        mainView.left - viewClosedBounds.left,
                        pivotRight - mainView.left
                    )
                }

                Direction.RIGHT_TO_LEFT -> {
                    val pivotLeft = viewClosedBounds.right - maxActionsWidth

                    Math.min(
                        mainView.right - pivotLeft,
                        viewClosedBounds.right - mainView.right
                    )
                }
                Direction.NONE -> 0
            }

    init {
        dragHelper = ViewDragHelper.create(this, 1.0f, dragHelperCallback)
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.DIRECTION_HORIZONTAL)

        gestureDetector = GestureDetectorCompat(context, mGestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isDragLocked) {
            return super.onInterceptTouchEvent(ev)
        }

        dragHelper.processTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)
        accumulateDragDist(ev)

        val couldBecomeClick = couldBecomeClick(ev) || couldBecomeActionClick(ev)
        val settling = dragHelper.viewDragState == ViewDragHelper.STATE_SETTLING
        val idleAfterScrolled = dragHelper.viewDragState == ViewDragHelper.STATE_IDLE && isScrolling

        lastKnownTouchX = ev.x

        return !couldBecomeClick && (settling || idleAfterScrolled)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //TODO(st235): assert view size

        //the only child from xml
        mainView = getChildAt(0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        swipeActionViewFactory.onOwnerBoundsChanged(w, h)
    }

    fun setItems(swipeActions: List<SwipeAction>) {
        viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                for (i in 0 until swipeActions.size) {
                    leftItems.add(swipeActions[i])
                    rightItems.add(swipeActions[swipeActions.size - i - 1])
                }

                leftItemsViews.addAll(swipeActionViewFactory.createLayout(leftItems,
                    SwipeActionViewFactory.Gravity.LEFT
                ))
                rightItemsViews.addAll(swipeActionViewFactory.createLayout(rightItems,
                    SwipeActionViewFactory.Gravity.RIGHT
                ))
                hudViewController.attachToParent(this@SwipeToActionLayout, swipeActions.first())

                bringChildToFront(mainView)

                maxActionsWidth = Math.min(width, height) * swipeActions.size
                viewClosedBounds.set(
                    mainView.left,
                    mainView.top,
                    mainView.right,
                    mainView.bottom
                )
            }
        })
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun open(animation: Boolean) {
        doesOpenBeforeInit = true
        isAborted = false

        if (animation) {
            state = State.OPENING
            dragHelper.smoothSlideViewTo(mainView, openedEdgePosition, viewClosedBounds.top)
        } else {
            state = State.OPEN
            dragHelper.abort()
        }

        ViewCompat.postInvalidateOnAnimation(this@SwipeToActionLayout)
    }

    fun close(animation: Boolean) {
        doesOpenBeforeInit = false
        isAborted = false

        if (animation) {
            state = State.CLOSING
            dragHelper.smoothSlideViewTo(mainView, viewClosedBounds.left, viewClosedBounds.top)
        } else {
            state = State.CLOSE
            dragHelper.abort()

            mainView.layout(
                viewClosedBounds.left,
                viewClosedBounds.top,
                viewClosedBounds.right,
                viewClosedBounds.bottom
            )
        }

        ViewCompat.postInvalidateOnAnimation(this@SwipeToActionLayout)
    }

    protected fun abort() {
        isAborted = true
        dragHelper.abort()
    }

    private fun onDirectionChanged(newDirection: Direction, oldDirection: Direction) {
    }

    private fun onStateChanged(newValue: State, oldValue: State) {
        when {
            oldValue == State.CLOSE && (newValue == State.OPENING) -> {
                for (i in 0 until leftItemsViews.size) {
                    val v = leftItemsViews[i]
                    v.offsetLeftAndRight(-v.width * (i + 1))
                }

                for (i in 0 until rightItemsViews.size) {
                    val v = rightItemsViews[i]
                    v.offsetLeftAndRight(v.width * (rightItemsViews.size - i))
                }
            }
            oldValue != State.DRAG_FULLY_OPENED && newValue == State.DRAG_FULLY_OPENED -> {
                hudViewController.revealAt(
                    if (lastKnownSwipeDirection == Direction.LEFT_TO_RIGHT) leftItemsViews.first() else rightItemsViews.last(),
                    lastKnownSwipeDirection
                )
            }
            oldValue == State.DRAG_FULLY_OPENED && newValue == State.CLOSING_MANUALLY -> {
                hudViewController.hideAt(
                    if (lastKnownSwipeDirection == Direction.LEFT_TO_RIGHT) rightItemsViews.last() else leftItemsViews.first(),
                    lastKnownSwipeDirection
                )
            }
            newValue == State.CLOSE -> {
                hudViewController.hide()
            }
        }
    }

    private fun couldBecomeClick(ev: MotionEvent): Boolean {
        return mainView.isIn(ev) && !shouldInitiateADrag()
    }

    private fun couldBecomeActionClick(ev: MotionEvent): Boolean {
        if (shouldInitiateADrag()) {
            return false
        }

        var isInAction = false
        for (v in leftItemsViews) isInAction = isInAction or v.isIn(ev)
        for (v in rightItemsViews) isInAction = isInAction or v.isIn(ev)
        return isInAction
    }

    private fun View.isIn(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        val withinVertical = this.top <= y && y <= this.bottom
        val withinHorizontal = this.left <= x && x <= this.right
        return withinVertical && withinHorizontal
    }

    private fun shouldInitiateADrag(): Boolean {
        val minDistToInitiateDrag = dragHelper.touchSlop.toFloat()
        return dragDist >= minDistToInitiateDrag
    }

    private fun accumulateDragDist(ev: MotionEvent) {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            dragDist = 0f
            return
        }
        val dragged = Math.abs(ev.x - lastKnownTouchX)
        dragDist += dragged
    }

    private inner class SwipeToActionGestureListener: GestureDetector.SimpleOnGestureListener() {
        private var hasDisallowed = false

        override fun onDown(e: MotionEvent): Boolean {
            isScrolling = false
            hasDisallowed = false
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            isScrolling = true
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            isScrolling = true
            lastKnownSwipeDirection = Direction.obtainBy(distanceX = distanceX)

            val leftThreshold = viewClosedBounds.left + maxActionsWidth
            val rightThreshold = viewClosedBounds.right - maxActionsWidth
            when {
                lastKnownSwipeDirection == Direction.LEFT_TO_RIGHT && mainView.left > leftThreshold ->
                    state = State.DRAG_FULLY_OPENED
                lastKnownSwipeDirection == Direction.RIGHT_TO_LEFT && mainView.right < rightThreshold ->
                    state = State.DRAG_FULLY_OPENED
                dragHelper.viewDragState == ViewDragHelper.STATE_DRAGGING
                        && lastKnownSwipeDirection == Direction.RIGHT_TO_LEFT
                        && mainView.left < leftThreshold ->
                    state = State.CLOSING_MANUALLY
                dragHelper.viewDragState == ViewDragHelper.STATE_DRAGGING
                        && lastKnownSwipeDirection == Direction.LEFT_TO_RIGHT
                        && mainView.right > rightThreshold ->
                    state = State.CLOSING_MANUALLY
            }

            if (parent != null) {
                val shouldDisallow: Boolean

                if (!hasDisallowed) {
                    shouldDisallow = distToClosestEdge >= minDistRequestDisallowParent
                    if (shouldDisallow) {
                        hasDisallowed = true
                    }
                } else {
                    shouldDisallow = true
                }

                // disallow parent to intercept touch event so that the layout will work
                // properly on RecyclerView or view that handles scroll gesture.
                parent.requestDisallowInterceptTouchEvent(shouldDisallow)
            }

            return false
        }
    }

    private inner class SwipeToActionViewHelperCallback: ViewDragHelper.Callback() {

        private val slideOffset: Float
            get() = when(lastKnownSwipeDirection) {
                Direction.LEFT_TO_RIGHT -> (viewClosedBounds.left - viewClosedBounds.left).toFloat() / maxActionsWidth
                Direction.RIGHT_TO_LEFT -> (viewClosedBounds.left - mainView.left).toFloat() / maxActionsWidth
                else -> 0F
            }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            isAborted = false

            if (isDragLocked)
                return false

            dragHelper.captureChildView(mainView, pointerId)
            return false
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val leftThreshold = viewClosedBounds.left + maxActionsWidth / 2
            val leftMax = viewClosedBounds.left + maxActionsWidth

            val rightThreshold = viewClosedBounds.right - maxActionsWidth/ 2
            val rightMax = viewClosedBounds.right - maxActionsWidth

            when (lastKnownSwipeDirection) {
                Direction.LEFT_TO_RIGHT -> if (releasedChild.left in (leftThreshold + 1) until leftMax) {
                    open(true)
                } else {
                    if (releasedChild.left > leftMax)
                        onLongActionClickListener?.invoke(leftItemsViews.first(), leftItems.first())
                    close(true)
                }
                Direction.RIGHT_TO_LEFT -> if (releasedChild.right in (rightMax + 1) until rightThreshold) {
                    open(true)
                } else {
                    if (releasedChild.right < rightMax)
                        onLongActionClickListener?.invoke(rightItemsViews.last(), rightItems.last())
                    close(true)
                }
            }
        }

        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            super.onEdgeDragStarted(edgeFlags, pointerId)
            if (isDragLocked) {
                return
            }

            dragHelper.captureChildView(mainView, pointerId)
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)

            for (i in 0 until leftItemsViews.size) {
                val v = leftItemsViews[i]
                val progress = changedView.left.toDouble() / maxActionsWidth.toDouble()
                if (progress > 1.0) continue
                val myBound = (i + 1) * Math.min(width, height)
                v.offsetLeftAndRight(Math.ceil(progress * myBound).toInt() - v.right)
            }

            for (i in 0 until rightItemsViews.size) {
                val v = rightItemsViews[i]
                val maxBound = (Math.min(width, height) * rightItemsViews.size).toDouble()
                val progress = (viewClosedBounds.right - changedView.right) / maxBound
                if (progress > 1.0) continue
                val finalLeft = i * Math.min(width, height)
                val boundLeft = (viewClosedBounds.right - maxActionsWidth) + finalLeft
                v.offsetLeftAndRight(boundLeft + Math.ceil((1 - progress) * (maxActionsWidth - finalLeft)).toInt() - v.left)
            }

            val isMoved = mainView.left != lastKnownMainViewLeftPosition

            if (onSwipeListener != null && isMoved) {
                if (mainView.left == viewClosedBounds.left && mainView.top == viewClosedBounds.top) {
                    onSwipeListener?.onClosed(this@SwipeToActionLayout)
                    //TODO(st235): fix it!!!
//                } else if (mainView.left == mRectMainOpen.left && mainView.top == mRectMainOpen.top) {
//                    onSwipeListener?.onOpened(this@SwipeToActionLayout)
                } else {
                    onSwipeListener?.onSlide(this@SwipeToActionLayout, slideOffset)
                }
            }

            lastKnownMainViewLeftPosition = mainView.left
            ViewCompat.postInvalidateOnAnimation(this@SwipeToActionLayout)
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            when (state) {
                ViewDragHelper.STATE_IDLE ->
                    if (mainView.left == viewClosedBounds.left) {
                        this@SwipeToActionLayout.state = State.CLOSE
                    } else {
                        this@SwipeToActionLayout.state = State.OPEN
                    }
            }
        }
    }
}
