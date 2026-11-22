# tvOS-style launcher for Google TV

A custom Android TV home screen with a tvOS look: big rounded app tiles, focus zoom
and white focus ring, a hero area showing the focused app, a background that
changes colour with the focused app, and a live clock.

This is a launcher, not a replacement OS. Google TV stays Android underneath.

## Build

1. Install Android Studio (Koala or newer).
2. Open the `TvOSLauncher` folder. Let Gradle sync (it creates the Gradle wrapper).
3. Build > Build APK(s). The APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## Install on the TV

1. On the TV: Settings > System > About, tap "Android TV OS build" 7 times.
2. Settings > System > Developer options > turn on USB debugging (and Wireless debugging if shown).
3. Find the TV's IP address in Settings > Network.
4. On your PC:

   adb connect <tv-ip>:5555
   adb install -r app/build/outputs/apk/debug/app-debug.apk

5. Press Home on the remote. Android asks which launcher to use. Pick "tvOS Home" and choose Always.

Some Google TV builds do not show the picker. In that case, disable the stock launcher (only after
installing this one):

   adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx

To go back to the stock home screen:

   adb shell pm enable com.google.android.apps.tv.launcherx
   adb uninstall com.example.tvoslauncher

Package names can differ by device. Check yours with:

   adb shell cmd package resolve-activity -a android.intent.action.MAIN -c android.intent.category.HOME

## Screensaver

The app includes an "Aurora" screensaver (slow drifting colours with a moving clock).
Turn it on with:

   adb shell settings put secure screensaver_components com.example.tvoslauncher/.AuroraDream
   adb shell settings put secure screensaver_enabled 1
   adb shell settings put system screen_off_timeout 600000

You can also pick it in Settings > Device Preferences > Screen saver, if your TV lists it.
The screensaver starts after the idle time set in that same screen.

## Using your own wallpaper or icons

Apple's own wallpapers, aerial videos and icons are Apple's artwork, so they are not bundled here.
The aurora wallpaper and icon plates are original. To use your own photo, draw it in
`AuroraBackground.kt` before the blobs (Canvas `drawImage`) and keep the vignette on top.

## Customising

- Colours, tile size and spacing: `HomeScreen.kt`
- Which apps appear and how icons are loaded: `AppRepository.kt`
- Change the applicationId in `app/build.gradle.kts` if you plan to publish it.
