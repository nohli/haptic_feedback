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
```

### Android-specific options

```dart
// Use native Android haptic constants (default: false)
// When true, uses HapticFeedbackConstants like CONFIRM, REJECT, etc.
// When false, uses custom vibration primitives that are more aligned
// with iOS haptics
await Haptics.vibrate(
  HapticsType.success,
  useAndroidHapticConstants: false,  // default
);

// On Android 13+, you can hint how the system should treat this vibration
// (alarm, communicationRequest, hardwareFeedback, media, notification, physicalEmulation, ringtone, touch, unknown)
await Haptics.vibrate(
  HapticsType.success,
  usage: HapticsUsage.media,
);
```

The `useAndroidHapticConstants` parameter (default: `false`) controls whether Android uses the system's native `HapticFeedbackConstants` (like `CONFIRM`, `REJECT`, `VIRTUAL_KEY`) when available. Set to `false` to use custom vibration primitives instead, which keep the feel closer to the iOS patterns.

The optional `usage` parameter is a hint for the system.
It can influence how the vibration is routed and which volume / haptics
settings control it (for example, notification vs touch feedback).

Use a concrete value whenever the vibration clearly matches one of the
defined categories (for example `HapticsUsage.notification` for reminders
or status updates), and keep the default `HapticsUsage.unknown` for simple
taps and other lightweight UI feedback.

## Platform Implementation

### iOS

Uses Apple's native haptic feedback APIs:
- `UINotificationFeedbackGenerator` for success, warning, and error
- `UIImpactFeedbackGenerator` for light, medium, heavy, rigid, and soft
- `UISelectionFeedbackGenerator` for selection

### Android

The plugin uses a multi-strategy approach for the best possible haptic experience:

#### Strategy 1: Native HapticFeedbackConstants (when enabled)

When `useAndroidHapticConstants: true`, the plugin uses Android's system-level haptic constants via `View.performHapticFeedback()`:

| Type      | Android Constant         | API Level |
|-----------|--------------------------|-----------|
| success   | `CONFIRM`                | ≥ 30      |
| error     | `REJECT`                 | ≥ 30      |
| light     | `VIRTUAL_KEY`            | ≥ 5       |
| medium    | `KEYBOARD_TAP`           | ≥ 8       |
| heavy     | `CONTEXT_CLICK`          | ≥ 23      |
| selection | `CLOCK_TICK`             | ≥ 21      |

Note: `warning`, `rigid`, and `soft` don't have direct Android mappings and fall back to primitives/waveforms.

#### Strategy 2: Haptic Primitives (API 30+)

When native constants aren't available or `useAndroidHapticConstants: false` (default), the plugin uses `VibrationEffect.Composition`, which is tuned to resemble the iOS patterns:

| Type      | Primitive(s)         | Description                              |
|-----------|----------------------|------------------------------------------|
| success   | CLICK × 2            | Two clicks with increasing intensity     |
| warning   | CLICK × 2            | Two clicks with decreasing intensity     |
| error     | CLICK + THUD + CLICK | Four-pulse pattern with accented pulse   |
| light     | TICK                 | Subtle, light feedback                   |
| medium    | CLICK                | Moderate feedback                        |
| heavy     | THUD                 | Strong, deep feedback                    |
| rigid     | CLICK                | Sharp, crisp feedback                    |
| soft      | SPIN*                | Gentle, longer feedback                  |
| selection | TICK                 | Subtle selection feedback                |

\* `SPIN` requires API 31+; falls back to `TICK` on API 30.

#### Strategy 3: Waveform Vibrations (API 26-29)

For devices without primitive support, the plugin uses `VibrationEffect.createWaveform()` with amplitude control.

#### Strategy 4: Legacy Vibration (API < 26)

Basic timing-only patterns for older devices.

For detailed timing specifications and implementation rationale, see [HAPTIC_PATTERNS.md](https://github.com/nohli/haptic_feedback/blob/main/HAPTIC_PATTERNS.md).

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
