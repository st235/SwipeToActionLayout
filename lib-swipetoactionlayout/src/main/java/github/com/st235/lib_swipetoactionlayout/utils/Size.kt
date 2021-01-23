package github.com.st235.lib_swipetoactionlayout.utils

internal class Size(
    width: Int,
    height: Int
) {

    var width: Int = width
    private set

    var height: Int = height
    private set

    fun set(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}