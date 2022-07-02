package github.com.st235.swipetoactionlayout

import java.util.*

data class ContactInfo(
    val name: String,
    val position: String,
    val isOnline: Boolean,
    val isAuthor: Boolean = false
) {

    val email: String
    get() {
        val raw = name.split(" ")
        return String.format(
            "%s.%s@hooli.xyz",
            raw[0].lowercase(Locale.getDefault()),
            raw[1].lowercase(Locale.getDefault())
        )
    }

}