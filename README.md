# haptic_feedback

A haptic feedback plugin for both iOS and Android.

While it utilizes [standard iOS haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS), it aims to emulate these same haptic patterns on Android for a consistent experience across platforms.

## Getting Started

### 1. Add the dependency

```shell
flutter pub add haptic_feedback
```

### 2. Use the plugin

```dart
final canVibrate = await Haptics.canVibrate();

await Haptics.vibrate(HapticsType.success);
await Haptics.vibrate(HapticsType.warning);
await Haptics.vibrate(HapticsType.error);

await Haptics.vibrate(HapticsType.light);
await Haptics.vibrate(HapticsType.medium);
await Haptics.vibrate(HapticsType.heavy);

await Haptics.vibrate(HapticsType.rigid);
await Haptics.vibrate(HapticsType.soft);

await Haptics.vibrate(HapticsType.selection);
```

## Automatic Permissions Inclusion

### Android VIBRATE Permission

When you integrate the `haptic_feedback` plugin into your Flutter project, it will automatically include the necessary `VIBRATE` permission in the final merged `AndroidManifest.xml` of your app. This is due to the permission being declared in the plugin's manifest.

#### What this means for you:

- **No Manual Action Required**: You don't have to add the `<uses-permission android:name="android.permission.VIBRATE"/>` permission to your app's `AndroidManifest.xml` manually. It will be automatically merged when building the app.

- **Transparency**: By using the `haptic_feedback` plugin, your app will request the `VIBRATE` permission. Ensure that you are aware of all permissions your app requires, especially if you publish it on app stores. Some users may be sensitive to app permissions, even if they don't require explicit consent.

- **Permission Overview**: To review all permissions that your app requests due to plugins and your own declarations, inspect the [final merged](https://stackoverflow.com/questions/74025731/where-is-the-merged-manifest-in-flutter-project) `AndroidManifest.xml` after a build. This will provide a comprehensive view of all permissions and other manifest entries.
