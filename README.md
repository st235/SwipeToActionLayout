<img src="images/showcase.gif" width="540" height="105">

# SwipeToActionLayout
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.st235/swipetoactionlayout/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.st235/swipetoactionlayout)

SwipeToActionLayout is a layout which helps to implement swipe to reveal behaviour. It is really easy to setup and maintain.

First of all, you need to download it, don't you? 🙂

## Download from ...

__Important: library was migrated from JCenter to MavenCentral__ 

It means that it may be necessary to add __mavenCentral__ repository to your repositories list

```groovy
allprojects {
    repositories {
        // your repositories

        mavenCentral()
    }
}
```

- Maven

```text
<dependency>
  <groupId>com.github.st235</groupId>
  <artifactId>swipetoactionlayout</artifactId>
  <version>X.X</version>
  <type>pom</type>
</dependency>
```

- Gradle

```text
implementation 'com.github.st235:swipetoactionlayout:X.X'
```

- Ivy

```text
<dependency org='com.github.st235' name='swipetoactionlayout' rev='X.X'>
  <artifact name='swipetoactionlayout' ext='pom' ></artifact>
</dependency>
```

P.S.: The latest code version is mentioned at the version badge at the top of this page.

## Usage

After you have downloaded the library, you need to integrate it. You should declare layout at your xml.

```xml
<github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:sal_gravity="right"
    app:sal_isFullActionSupported="false"
    app:sal_items="@menu/swipe_to_action_menu"
    app:sal_shouldVibrateOnQuickAction="false">

    ... draggable child ...

</github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout>
```

Xml declaration supports the following attributes:

| property | type | description |
| ----- | ----- | ----- |
| **sal_gravity** | enum | gravity of your action menu. could be without rtl support, ie left or right, and with rtl support, ie start and end |
| **sal_isFullActionSupported** | boolean | allows you to add support of quick action, which will be revealed on full swipe |
| **sal_shouldVibrateOnQuickAction** | boolean | when quick action will be applied view can perform haptic feedback. set this flag as true if you want to support it. |
| **sal_items** | reference | reference to your xml menu file |

If you decided to use xml-declared menu then you should create menu file by the next rules:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto" xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/call"
        android:icon="@drawable/ic_call_black_18dp"
        android:background="@drawable/action_background"
        android:iconTint="@color/iconTint"/>

    <item
        android:id="@+id/email"
        android:icon="@drawable/ic_email_black_18dp"
        android:background="@drawable/action_background"
        android:iconTint="@color/iconTint" />

    <item
        android:id="@+id/delete"
        android:icon="@drawable/ic_delete_black_18dp"
        android:background="@drawable/action_background"
        android:iconTint="@color/colorAccent" />
</menu>
```

An item supports the following attributes:

| property | type | description |
| ----- | ----- | ----- |
| **id** | id | an id of your action |
| **title** | text | a supporting text, will be shown under icon |
| **icon** | reference | a reference to drawable |
| **titleTextColor** | color | a color of your title |
| **background** | reference | a reference to your background resource |
| **iconTint** | color | tint to your icon, allows to change color of it |

PS: all these properties are start with `android` prefix, for example `android:background`

Is is also possible to apply actions programmatically

```kotlin
        swipeToActionLayout.actions =
                listOf(
                        SwipeAction(0xFFFBDAEE.toInt(), R.drawable.baseline_call_24, getString(R.string.action_call), Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFFFF7A4.toInt(), R.drawable.baseline_email_24, getString(R.string.action_email), Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFC0E7F6.toInt(), R.drawable.baseline_duo_24, getString(R.string.action_duo), Color.BLACK, Color.BLACK)
                )
```

To listen lifecycle callbacks from `SwipeToActionLayout` you should implement `SwipeMenuListener`

```Kotlin

swipeToActionLayout.menuListener = MenuListener()

private inner class MenuListener: SwipeMenuListener {

        override fun onClosed(view: View) {
            // empty on purpose
        }

        override fun onOpened(view: View) {
            // empty on purpose
        }

        override fun onFullyOpened(view: View, quickAction: SwipeAction) {
            // empty on purpose
        }

     override fun onActionClicked(view: View, action: SwipeAction) {
         Toast.makeText(this@MainActivity, "On clicked on: ${action.text}", Toast.LENGTH_SHORT).show()
         swipeToActionLayout.close()
     }
 }

```

And that is it. Enjoy it!

### License

```text
MIT License

Copyright (c) 2019 Alexander Dadukin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
