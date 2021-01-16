package github.com.st235.swipetoactionlayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import github.com.st235.lib_swipetoactionlayout.ActionBindHelper
import github.com.st235.lib_swipetoactionlayout.SwipeAction
import github.com.st235.lib_swipetoactionlayout.SwipeMenuListener
import github.com.st235.swipetoactionlayout.identicon.IdenticonDrawable
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_swipetoaction.view.*

typealias OnActionClicked = (contact: ContactInfo, action: SwipeAction) -> Unit

class ContactsAdapter(
    private val contacts: MutableList<ContactInfo>,
    private val onActionClicked: OnActionClicked
): RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private val actionsBindHelper = ActionBindHelper()

    fun remove(contact: ContactInfo) {
        val index = contacts.indexOf(contact)
        contacts.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_swipetoaction, parent, false)
        return ContactsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]
        actionsBindHelper.bind(contact.name, holder.swipeToActionLayout)
        holder.bind(contact)
    }

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SwipeMenuListener {
        val icon = itemView.icon
        val isOnline = itemView.isOnline
        val title = itemView.title
        val description = itemView.description

        val swipeToActionLayout = itemView.swipeToAction

        fun bind(contact: ContactInfo) {
            swipeToActionLayout.menuListener = this

            title.text = contact.name

            val hue = (contact.name.hashCode()) % 255.0f + 125.0f
            val activeColor = ColorUtils.HSLToColor(floatArrayOf(hue, 0.35f, 0.65f))
            val inactiveColor = 0xFFF0F0F0.toInt()

            icon.setImageDrawable(IdenticonDrawable(contact.name, activeColor, inactiveColor))

            if (contact.isOnline) {
                isOnline.visibility = View.VISIBLE
            } else {
                isOnline.visibility = View.GONE
            }

            description.text = String.format("%s, %s", contact.position, contact.email)

            if (contact.isAuthor) {
                isOnline.setImageResource(R.drawable.ic_favorite_black_18dp)
                swipeToActionLayout.setActionsRes(R.menu.special_menu)
            } else {
                isOnline.setImageResource(R.drawable.circle_green)
                swipeToActionLayout.setActionsRes(R.menu.swipe_to_action_menu)
            }
        }

        override fun onClosed(view: View) {
            // empty on purpose
        }

        override fun onOpened(view: View) {
            val contact = contacts[adapterPosition]
            actionsBindHelper.closeOtherThan(contact.name)
        }

        override fun onFullyOpened(view: View, quickAction: SwipeAction) {
            // empty on purpose
        }

        override fun onActionClicked(view: View, action: SwipeAction) {
            onActionClicked(contacts[adapterPosition], action)
            swipeToActionLayout.close()
        }
    }
}