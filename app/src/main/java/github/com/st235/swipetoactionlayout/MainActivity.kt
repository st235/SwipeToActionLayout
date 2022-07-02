package github.com.st235.swipetoactionlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var widgetShowcase: Button
    private lateinit var usageExample: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        widgetShowcase = findViewById(R.id.widget_showcase)
        usageExample = findViewById(R.id.usage_example)

        widgetShowcase.setOnClickListener {
            val intent = Intent(this, ShowcaseActivity::class.java)
            startActivity(intent)
        }

        usageExample.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
    }
}
