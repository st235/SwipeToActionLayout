package github.com.st235.swipetoactionlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import github.com.st235.lib_swipetoactionlayout.SwipeAction
import github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout

class ShowcaseActivity : AppCompatActivity() {

    private lateinit var swipeToActionLayout: SwipeToActionLayout
    private lateinit var vibrateCheckBox: CheckBox
    private lateinit var quickActionCheckBox: CheckBox
    private lateinit var left: Button
    private lateinit var right: Button
    private lateinit var start: Button
    private lateinit var end: Button
    private lateinit var close: Button
    private lateinit var open: Button
    private lateinit var openFully: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)

        swipeToActionLayout = findViewById(R.id.swipe_to_action_layout)
        vibrateCheckBox = findViewById(R.id.vibrate_check_box)
        quickActionCheckBox = findViewById(R.id.quick_action_check_box)
        left = findViewById(R.id.left)
        right = findViewById(R.id.right)
        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        close = findViewById(R.id.close)
        open = findViewById(R.id.open)
        openFully = findViewById(R.id.open_fully)

        swipeToActionLayout.actions =
            listOf(
                SwipeAction.withBackgroundColor(
                    actionId = R.id.action_call,
                    iconId = R.drawable.ic_call_black_18dp,
                    text = "Call",
                    backgroundColor = 0xFF455A64.toInt()
                ),
                SwipeAction.withBackgroundColor(
                    actionId = R.id.action_email,
                    iconId = R.drawable.ic_email_black_18dp,
                    text = "Email",
                    backgroundColor = 0xFF37474F.toInt()
                ),
                SwipeAction.withBackgroundColor(
                    actionId = R.id.action_delete,
                    iconId = R.drawable.ic_delete_black_18dp,
                    text = "Remove",
                    iconTint = 0xFFEEEEEE.toInt(),
                    textColor = 0xFFEEEEEE.toInt(),
                    backgroundColor = 0xFF263238.toInt()
                )
            )

        vibrateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            swipeToActionLayout.shouldVibrateOnQuickAction = isChecked
        }

        quickActionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            swipeToActionLayout.isFullActionSupported = isChecked
        }

        left.setOnClickListener {
            swipeToActionLayout.gravity = SwipeToActionLayout.MenuGravity.LEFT
        }

        right.setOnClickListener {
            swipeToActionLayout.gravity = SwipeToActionLayout.MenuGravity.RIGHT
        }

        start.setOnClickListener {
            swipeToActionLayout.gravity = SwipeToActionLayout.MenuGravity.START
        }

        end.setOnClickListener {
            swipeToActionLayout.gravity = SwipeToActionLayout.MenuGravity.END
        }

        open.setOnClickListener {
            swipeToActionLayout.open()
        }

        close.setOnClickListener {
            swipeToActionLayout.close()
        }

        openFully.setOnClickListener {
            swipeToActionLayout.fullyOpen()
        }
    }
}