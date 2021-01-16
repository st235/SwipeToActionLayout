package github.com.st235.swipetoactionlayout.identicon

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.math.min

class IdenticonDrawable(
    private val text: String,
    @ColorInt private val activeCellsColor: Int,
    @ColorInt private val inactiveCellsColor: Int,
    @IntRange(from = 1L) private val fieldSize: Int = 5
): Drawable() {

    private companion object {
        const val HASH_ALGORITHM = "SHA-256"
    }

    private var cellSize: Int = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val hash: ByteArray

    init {
        hash = generateHash(text)
    }

    override fun onBoundsChange(bounds: Rect?) {
        val width = bounds?.width() ?: 0
        val height = bounds?.height() ?: 0

        val boundSize = min(width, height)

        cellSize = boundSize / fieldSize
    }

    override fun draw(canvas: Canvas) {
        for (row in 0 until fieldSize) {
            for (column in 0 until fieldSize) {
                val x: Float = (column * cellSize).toFloat()
                val y: Float = (row * cellSize).toFloat()
                paint.color = obtainColorForCell(column, row)
                canvas.drawRect(x, y, x + cellSize, y + cellSize, paint)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return paint.alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @ColorInt
    private fun obtainColorForCell(column: Int, row: Int): Int {
        val halfRowIndex = fieldSize / 2
        val shouldMirror = (column > halfRowIndex)
        val newColumn = if (shouldMirror) fieldSize - column - 1 else column

        val isActive: Boolean = getByte(row * (halfRowIndex + 1) + newColumn) >= 0
        return if (isActive) activeCellsColor else inactiveCellsColor
    }

    private fun generateHash(text: String): ByteArray {
        try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            digest.update(text.toByteArray())
            return digest.digest()
        } catch (exception: NoSuchAlgorithmException) {
            throw RuntimeException(exception)
        }
    }

    private fun getByte(index: Int): Byte {
        return hash[index % hash.size]
    }
}