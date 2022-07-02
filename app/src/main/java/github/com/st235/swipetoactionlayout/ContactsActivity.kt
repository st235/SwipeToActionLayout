package github.com.st235.swipetoactionlayout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val adapter = ContactsAdapter(
        mutableListOf(
            ContactInfo("John Doe", "CEO", false),
            ContactInfo("Waylon Dalton", "CTO", false),
            ContactInfo("Marcus Cruz", "VP of Engineering", true),
            ContactInfo("Eddie Randolph", "VP of Design", true),
            ContactInfo("Hadassah Hartman", "Sales Director", false),
            ContactInfo("Justine Henderson", "Director of Engineering", true),
            ContactInfo("Thalia Cobb", "Staff Software Engineer", true),
            ContactInfo("Angela Walker", "Staff Software Engineer", true),
            ContactInfo("Joanna Shaffer", "Senior Product Designer", false),
            ContactInfo("Abdullah Lang", "Senior Product Owner", false),
            ContactInfo("Mathias Little", "Senior Software Engineer", true),
            ContactInfo("Lia Shelton", "Software Engineer", true),
            ContactInfo("Jonathon Sheppard", "Product Designer", true),
            ContactInfo("Will Odom", "Product Designer", true),
            ContactInfo("Adriana Fry", "Software Engineer", true),
            ContactInfo("Elaina Vasquez", "Intern Designer", true),
            ContactInfo("Landyn Martin", "Product Designer", true),
            ContactInfo("Madalyn Savage", "Software Engineer", true),
            ContactInfo("Cindy Moss", "Sales Manager", true),
            ContactInfo("Alexander Dadukin", "Cats Lover", true, true),
            ContactInfo("Mathew Tapia", "Software Engineer", true),
            ContactInfo("Ayanna Shields", "Copyrighter", false)
        )
    ) { item, action ->
        when (action.actionId) {
            R.id.call -> call(item)
            R.id.email -> email(item)
            R.id.delete -> remove(item)
            R.id.star -> openRepo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun call(item: ContactInfo) {
        Toast.makeText(this, "Calling to: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    private fun email(item: ContactInfo) {
        Toast.makeText(this, "Email to: ${item.email}", Toast.LENGTH_SHORT).show()
    }

    private fun remove(item: ContactInfo) {
        adapter.remove(item)
    }

    private fun openRepo() {
        val url = "https://github.com/st235/SwipeToActionLayout"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
}