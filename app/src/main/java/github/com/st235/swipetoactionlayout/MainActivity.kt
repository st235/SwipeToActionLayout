package github.com.st235.swipetoactionlayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.lib_swipetoactionlayout.SwipeAction
import github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout

class MainActivity : AppCompatActivity() {

    private lateinit var swipeToActionLayout: SwipeToActionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeToActionLayout = findViewById(R.id.swipeToActionLayout)

        swipeToActionLayout.setItems(
                listOf(
                        SwipeAction(0xFFFBDAEE.toInt(), R.drawable.baseline_call_24, R.string.action_call, Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFFFF7A4.toInt(), R.drawable.baseline_email_24, R.string.action_email, Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFC0E7F6.toInt(), R.drawable.baseline_duo_24, R.string.action_duo, Color.BLACK, Color.BLACK)
                )
        )
    }
}
