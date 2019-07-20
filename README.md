<img src="https://raw.githubusercontent.com/st235/SwipeToActionLayout/master/images/showcase.gif" width="719" height="117">

# SwipeToActionLayout
[![Download](https://api.bintray.com/packages/st235/maven/swipetoactionlayout/images/download.svg)](https://bintray.com/st235/maven/swipetoactionlayout/_latestVersion)

Another one swipe to reveal like menu 🎭

## Download from ...

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

P.S.: Check out latest version code in badge at the top of this page.

## Usage

```kotlin
        swipeToActionLayout.setItems(
                listOf(
                        SwipeAction(0xFFFBDAEE.toInt(), R.drawable.baseline_call_24, getString(R.string.action_call), Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFFFF7A4.toInt(), R.drawable.baseline_email_24, getString(R.string.action_email), Color.BLACK, Color.BLACK),
                        SwipeAction(0xFFC0E7F6.toInt(), R.drawable.baseline_duo_24, getString(R.string.action_duo), Color.BLACK, Color.BLACK)
                )
        )
```

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
