package github.com.st235.swipetoactionlayout

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.lib_swipetoactionlayout.SwipeAction
import github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout

class MainActivity : AppCompatActivity() {

    private lateinit var deprecatedSwipeToActionLayout: SwipeToActionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        deprecatedSwipeToActionLayout = findViewById(R.id.swipeToActionLayout)
        deprecatedSwipeToActionLayout.isFullActionSupported = true
//        deprecatedSwipeToActionLayout.thresholdToOpenView = 0.0F

        deprecatedSwipeToActionLayout.actions =
                listOf(
                        SwipeAction(0, ColorDrawable(0xFFFBDAEE.toInt()), R.drawable.baseline_call_24, getString(R.string.action_call), Color.BLACK, Color.BLACK),
                        SwipeAction(1, ColorDrawable(0xFFFFF7A4.toInt()), R.drawable.baseline_email_24, getString(R.string.action_email), Color.BLACK, Color.BLACK),
                        SwipeAction(3, ColorDrawable(0xFFC0E7F6.toInt()), R.drawable.baseline_duo_24, getString(R.string.action_duo), Color.BLACK, Color.BLACK)
                )
    }
}
