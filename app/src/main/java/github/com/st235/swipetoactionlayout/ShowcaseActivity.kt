package github.com.st235.swipetoactionlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import github.com.st235.lib_swipetoactionlayout.SwipeAction
import github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout
import kotlinx.android.synthetic.main.activity_showcase.*

class ShowcaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)

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

        vibrateCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            swipeToActionLayout.shouldVibrateOnQuickAction = isChecked
        }

        quickActionCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
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