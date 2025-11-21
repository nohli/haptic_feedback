# haptic_feedback

A haptic feedback plugin for both iOS and Android.

While it utilizes [standard iOS haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS), it aims to emulate these same haptic patterns on Android for a consistent experience across platforms.

For more information on using the package in your Flutter app, you can read the article [Use haptic feedback to make your Flutter apps engaging](https://blog.kamranbekirov.com/blog/haptic) by Kamran. He provides detailed explanations on haptic feedback and when to use each type.

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

// On Android 13+, you can hint how the system should treat this vibration
// (alarm, communicationRequest, hardwareFeedback, media, notification, physicalEmulation, ringtone, touch, unknown)
await Haptics.vibrate(
  HapticsType.success,
  usage: HapticsUsage.media,
);
```

The optional `usage` parameter is a hint for the system.
It can influence how the vibration is routed and which volume / haptics
settings control it (for example, notification vs touch feedback).

Use a concrete value whenever the vibration clearly matches one of the
defined categories (for example `HapticsUsage.notification` for reminders
or status updates), and keep the default `HapticsUsage.unknown` for simple
taps and other lightweight UI feedback.

## Testing

When testing widgets that use haptic feedback, keep in mind that `defaultTargetPlatform` returns `TargetPlatform.android` in test environments regardless of the host platform. This means `Haptics.canVibrate()` may return `true` in tests even when running on non-mobile platforms.

To test widgets that use haptic feedback, you can:

1. **Use `debugDefaultTargetPlatformOverride`** to test platform-specific behavior:

```dart
import 'package:flutter/foundation.dart';

void main() {
  testWidgets('test on iOS', (tester) async {
    debugDefaultTargetPlatformOverride = TargetPlatform.iOS;
    
    // Your test code here
    
    debugDefaultTargetPlatformOverride = null; // Clean up
  });
}
```

2. **Mock the platform interface** in your tests:

```dart
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHapticFeedbackPlatform extends Mock
    with MockPlatformInterfaceMixin
    implements HapticFeedbackPlatform {}

void main() {
  testWidgets('my widget test', (tester) async {
    final mockPlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = mockPlatform;
    
    when(() => mockPlatform.canVibrate()).thenAnswer((_) async => true);
    when(() => mockPlatform.vibrate(any())).thenAnswer((_) async {});
    
    // Your test code here
  });
}
```

## Automatic Permissions Inclusion

### Android VIBRATE Permission

When you integrate the `haptic_feedback` plugin into your Flutter project, it will automatically include the necessary `VIBRATE` permission in the final merged `AndroidManifest.xml` of your app. This is due to the permission being declared in the plugin's manifest.

#### What this means for you:

- **No Manual Action Required**: You don't have to add the `<uses-permission android:name="android.permission.VIBRATE"/>` permission to your app's `AndroidManifest.xml` manually. It will be automatically merged when building the app.

- **Transparency**: By using the `haptic_feedback` plugin, your app will request the `VIBRATE` permission. Ensure that you are aware of all permissions your app requires, especially if you publish it on app stores. Some users may be sensitive to app permissions, even if they don't require explicit consent.

- **Permission Overview**: To review all permissions that your app requests due to plugins and your own declarations, inspect the [final merged](https://stackoverflow.com/questions/74025731/where-is-the-merged-manifest-in-flutter-project) `AndroidManifest.xml` after a build. This will provide a comprehensive view of all permissions and other manifest entries.
