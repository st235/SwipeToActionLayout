package github.com.st235.lib_swipetoactionlayout

interface OnSwipeListener {
    fun onClosed(view: SwipeToActionLayout)
    fun onOpened(view: SwipeToActionLayout)
    fun onSlide(view: SwipeToActionLayout, slideOffset: Float)
}
